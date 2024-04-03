package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FaultEventScenarioDao;
import cz.cvut.kbss.analysis.dao.FaultTreeDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.model.diagram.Rectangle;
import cz.cvut.kbss.analysis.model.fta.CutSetExtractor;
import cz.cvut.kbss.analysis.model.fta.FTAMinimalCutSetEvaluation;
import cz.cvut.kbss.analysis.model.fta.FtaEventType;
import cz.cvut.kbss.analysis.model.fta.GateType;
import cz.cvut.kbss.analysis.service.util.FaultTreeTraversalUtils;
import cz.cvut.kbss.analysis.service.util.Pair;
import cz.cvut.kbss.analysis.util.Vocabulary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FaultTreeRepositoryService extends BaseRepositoryService<FaultTree> {

    private final FaultTreeDao faultTreeDao;
    private final FaultEventScenarioDao faultEventScenarioDao;
    private final FaultEventRepositoryService faultEventRepositoryService;
    private final FunctionRepositoryService functionRepositoryService;
    private final IdentifierService identifierService;

    private final ThreadLocal<Set<Behavior>> visitedBehaviors = new ThreadLocal<>();

    @Autowired
    public FaultTreeRepositoryService(@Qualifier("defaultValidator") Validator validator,
                                      FaultTreeDao faultTreeDao,
                                      FaultEventScenarioDao faultEventScenarioDao,
                                      FaultEventRepositoryService faultEventRepositoryService,
                                      FunctionRepositoryService functionRepositoryService,
                                      IdentifierService identifierService
    ) {
        super(validator);
        this.faultTreeDao = faultTreeDao;
        this.faultEventScenarioDao = faultEventScenarioDao;
        this.faultEventRepositoryService = faultEventRepositoryService;
        this.functionRepositoryService = functionRepositoryService;
        this.identifierService = identifierService;
    }

    @Override
    protected GenericDao<FaultTree> getPrimaryDao() {
        return faultTreeDao;
    }

    @Transactional
    public void createTree(FaultTree faultTree){
        if(faultTree.getManifestingEvent() != null){
            faultTree.getManifestingEvent().setRectangle(new Rectangle());
        }
        persist(faultTree);
    }

    @Transactional(readOnly = true)
    @Override
    public FaultTree findRequired(URI id) {
        FaultTree ft = super.findRequired(id);
        // remove component children
        Set<Item> items = ft.getAllEvents().stream().map(e -> e.getSupertypes())
                .filter(ts -> ts!= null)
                .flatMap(ts -> ts.stream())
                .map(Event::getBehavior)
                .filter(ts -> ts!= null)
                .map(Behavior::getItem)
                .filter(c -> c != null).collect(Collectors.toSet());
        for(Item i : items){
            items.forEach(c -> c.setComponents(null));
        }
        return ft;
    }

    @Transactional
    @Override
    public FaultTree findRequired(URI id) {
        FaultTree faultTree = super.findRequired(id);
        setReferences(faultTree);
        return faultTree;
    }

    public void setReferences(FaultTree faultTree){
        if(faultTree.getManifestingEvent() == null)
            return;

        Stack<Pair<URI,FaultEvent>> stack = new Stack<>();
        stack.add(Pair.of(null,faultTree.getManifestingEvent()));
        while(!stack.isEmpty()){
            Pair<URI,FaultEvent> p = stack.pop();
            FaultEvent fe = p.getSecond();
            faultEventRepositoryService.setExternalReference(p.getFirst(), fe);
            if(fe.getChildren() == null)
                continue;
            fe.getChildren().forEach(c -> stack.push(Pair.of(fe.getUri(), c)));
        }
    }

    @Transactional
    public FaultTree findWithPropagation(URI faultTreeUri) {
        log.info("> findWithPropagation - {}", faultTreeUri);

        FaultTree faultTree = findRequired(faultTreeUri);

        log.debug("Propagating probabilities through the tree");
        propagateProbabilities(faultTree);

        update(faultTree);

        return faultTree;
    }

    @Override
    protected void prePersist(FaultTree instance) {
        super.prePersist(instance);

        URI faultEventUri = instance.getManifestingEvent().getUri();
        if (faultEventUri != null) {
            log.info("Reusing fault event - {}", faultEventUri);
            FaultEvent faultEvent = faultEventRepositoryService.findRequired(faultEventUri);
            instance.setManifestingEvent(faultEvent);
        }
    }

    private void propagateProbabilities(FaultTree faultTree) {
        log.info("> propagateProbabilities - {}", faultTree);

        Double recomputedProbability = faultEventRepositoryService.propagateProbability(faultTree.getManifestingEvent());

        log.info("< propagateProbabilities - {}", recomputedProbability);
    }

    @Transactional(readOnly = true)
    public List<FaultEvent> getReusableEvents(URI faultTreeUri) {
        log.info("> getReusableEvents - {}", faultTreeUri);

        FaultTree faultTree = findRequired(faultTreeUri);
        List<FaultEvent> allEvents = faultEventRepositoryService.findAll();

        List<FaultEvent> treeEvents = getTreeEvents(faultTree);
        allEvents.removeAll(treeEvents);

        log.info("< getReusableEvents");
        return allEvents;
    }

    @Transactional(readOnly = true)
    public List<List<FaultEvent>> getTreePaths(URI faultTreeUri) {
        log.info("> getTreePaths - {}", faultTreeUri);

        FaultTree faultTree = findRequired(faultTreeUri);

        Set<FaultEvent> leafEvents = FaultTreeTraversalUtils.getLeafEvents(faultTree.getManifestingEvent());

        List<List<FaultEvent>> treePaths = leafEvents.parallelStream()
                .map(leaf -> FaultTreeTraversalUtils.rootToLeafPath(faultTree.getManifestingEvent(), leaf.getUri()))
                .collect(Collectors.toList());

        log.info("< getTreePaths - {}", treePaths);
        return treePaths;
    }

    @Transactional(readOnly = true)
    public List<List<FaultEvent>> getTreePathsAggregate() {
        log.info("> getTreePathsAggregate");

        List<FaultTree> trees = findAll();
        List<List<FaultEvent>> treePathsAggregate = trees.stream()
                .filter(t -> !faultEventRepositoryService.isRootEventReused(t.getManifestingEvent()))
                .map(t -> getTreePaths(t.getUri()))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        log.info("< getTreePathsAggregate");
        return treePathsAggregate;
    }

    @Transactional
    public FailureModesTable createFailureModesTable(URI faultTreeUri, FailureModesTable failureModesTable) {
        log.info("> createFailureModesTable - {}, {}", faultTreeUri, failureModesTable);

        FaultTree faultTree = findRequired(faultTreeUri);
        faultTree.setFailureModesTable(failureModesTable);
        failureModesTable.setFaultTree(faultTree);

        update(faultTree);

        log.info("< createFailureModesTable - {}", failureModesTable);
        return failureModesTable;
    }

    @Transactional(readOnly = true)
    public FailureModesTable getFailureModesTable(URI faultTreeUri) {
        return findRequired(faultTreeUri).getFailureModesTable();
    }

    private List<FaultEvent> getTreeEvents(FaultTree tree) {
        List<FaultEvent> treeEvents = new ArrayList<>();
        getTreeEventsRecursive(tree.getManifestingEvent(), treeEvents);
        return treeEvents;
    }

    private void getTreeEventsRecursive(FaultEvent node, List<FaultEvent> eventList) {
        if (node != null) {
            eventList.add(node);
            node.getChildren()
                    .forEach(child -> getTreeEventsRecursive(child, eventList));
        }
    }

    @Transactional
    public FaultTree generateFunctionDependencyTree(URI functionUri, String faultTreeName) throws URISyntaxException {
        initTreeGeneration();
        Function function = functionRepositoryService.findRequired(functionUri);
        FaultEvent faultEvent;

        faultEvent = transferBehaviorToFaultEvent(function,null);
        FaultTree faultTree = new FaultTree();
        faultTree.setName(faultTreeName);
        faultTree.setManifestingEvent(faultEvent);

        if(!function.getChildBehaviors().isEmpty() || !functionRepositoryService.getImpairingBehaviors(functionUri).isEmpty() || !function.getRequiredBehaviors().isEmpty()) {
            processBehavior(function, faultEvent);
            faultEvent.setEventType(FtaEventType.INTERMEDIATE);
        }
        cleanTreeGeneration();
        createTree(faultTree);
        return faultTree;
    }

    private void processBehavior(Behavior behavior, FaultEvent parentFaultEvent) throws URISyntaxException {
        if(!addVisited(behavior))
            return;
        Set<FaultEvent> faultEvents = new LinkedHashSet<>();
        List<Behavior> impairingBehaviors = functionRepositoryService.getImpairingBehaviors(behavior.getUri());

        if(!behavior.getChildBehaviors().isEmpty() ){
            processChildBehaviors(behavior, parentFaultEvent);
        }

        for (Behavior requiredBehavior : behavior.getRequiredBehaviors()) {
            if(isVisited(requiredBehavior))
                continue;
            FaultEvent tmp = transferBehaviorToFaultEvent(requiredBehavior, parentFaultEvent);
            faultEvents.add(tmp);
            processBehavior(requiredBehavior, tmp);
        }

        if (!impairingBehaviors.isEmpty()) {
            for (Behavior impairingBehavior : impairingBehaviors) {
                if(isVisited(impairingBehavior) || impairingBehavior.isFailureModeCause()) continue;
                faultEvents.add(processImpairingBehavior(impairingBehavior, parentFaultEvent));
            }
        }

        setFaultEventTypes(faultEvents.size() == 0, parentFaultEvent);
        removeVisited(behavior);
        parentFaultEvent.addChildren(faultEvents);
    }

    private FaultEvent transferBehaviorToFaultEvent(Behavior behavior, FaultEvent parentEvent) throws URISyntaxException {
        URI faultEventUri = createUri(behavior, parentEvent, "");
        URI faultEventUri1 = createUri(behavior, parentEvent, "e");
        URI faultEventUri2 = createUri(behavior, parentEvent, "f");

        if (faultEventRepositoryService.existsInPersistenceContext(faultEventUri)) {
            return faultEventRepositoryService.findRequired(faultEventUri);
        } else if(faultEventRepositoryService.existsInPersistenceContext(faultEventUri1)){
            return faultEventRepositoryService.findRequired(faultEventUri1);
        } else if(faultEventRepositoryService.existsInPersistenceContext(faultEventUri2)){
            return faultEventRepositoryService.findRequired(faultEventUri2);
        } else {
            FaultEvent faultEvent = FaultEvent.create();

            faultEvent.setUri(faultEventUri);
            faultEvent.setBehavior(behavior);


            if (behavior instanceof Function) {
                faultEvent.setName(behavior.getName() + " fails");
                setFaultEventTypes(behavior, faultEvent);
            } else if (behavior instanceof FailureMode) {
                faultEvent.setName(behavior.getName());
                setFaultEventTypes(true, faultEvent);
            }
            faultEventRepositoryService.persist(faultEvent);
            return faultEvent;
        }
    }

    private URI createUri(Behavior behavior, FaultEvent parentEvent, String type) throws URISyntaxException {
        String behaviorUri = behavior.getUri().toString();
        if(parentEvent == null){
            return new URI(identifierService.composeIdentifier(Vocabulary.s_c_fault_event
                    , behaviorUri.substring(behaviorUri.lastIndexOf("/") + 1)) + type);
        }else{
            String parentBehaviorUri = parentEvent.getUri().toString();
            return new URI(identifierService.composeIdentifier(Vocabulary.s_c_fault_event
                    , behaviorUri.substring(behaviorUri.lastIndexOf("/") + 1)) + parentBehaviorUri.split("instance")[1] + type);
        }
    }

    private FaultEvent processImpairingBehavior(Behavior impairingBehavior, FaultEvent impairedBehaviorEvent) throws URISyntaxException {
        FaultEvent faultEvent;
        if(impairingBehavior.getBehaviorType() == BehaviorType.AtomicBehavior && impairedBehaviorEvent.getBehavior() instanceof Function) {
            faultEvent = transferBehaviorToFaultEvent(impairingBehavior, impairedBehaviorEvent);
        }else{
            URI faultEventUri = createUri(impairingBehavior, impairedBehaviorEvent, "");
            URI faultEventUriTypeEvent = createUri(impairingBehavior, impairedBehaviorEvent, "e");

            if(faultEventRepositoryService.existsInPersistenceContext(faultEventUri)) {
                faultEvent = faultEventRepositoryService.findRequired(faultEventUri);
            }else if(faultEventRepositoryService.existsInPersistenceContext(faultEventUriTypeEvent)){
                faultEvent = faultEventRepositoryService.findRequired(faultEventUriTypeEvent);
            }else {
                faultEvent = FaultEvent.create();
                faultEvent.setUri(faultEventUri);
                faultEvent.setBehavior(impairingBehavior);
                if(impairingBehavior.getBehaviorType() == BehaviorType.AtomicBehavior){
                    faultEventUri = createUri(impairingBehavior, impairedBehaviorEvent, "e");
                    faultEvent.setUri(faultEventUri);
                    faultEvent.setName(impairingBehavior.getName() + " event");
                    setFaultEventTypes(true, faultEvent);
                }else{
                    faultEventUri = createUri(impairingBehavior, impairedBehaviorEvent, "");
                    faultEvent.setUri(faultEventUri);
                    faultEvent.setName(impairingBehavior.getName());
                    faultEvent.setEventType(FtaEventType.INTERMEDIATE);
                    faultEvent.setGateType(impairingBehavior.getBehaviorType() == BehaviorType.OrBehavior ? GateType.OR : GateType.AND);

                    for (Behavior behaviorChild : impairingBehavior.getChildBehaviors()) {
                        if(behaviorChild.isFailureModeCause()) continue;

                        FaultEvent faultEventChild = FaultEvent.create();
                        faultEventChild.setBehavior(behaviorChild);
                        faultEventUri = createUri(behaviorChild, faultEvent, "e");
                        if (faultEventRepositoryService.existsInPersistenceContext(faultEventUri)) {
                            faultEventChild = faultEventRepositoryService.findRequired(faultEventUri);
                        } else {
                            faultEventChild.setUri(faultEventUri);
                            faultEventChild.setBehavior(behaviorChild);
                            faultEventChild.setName(behaviorChild.getName() + " event");
                            faultEventChild.setEventType(FtaEventType.BASIC);
                            faultEventChild.setGateType(null);
                            faultEventChild.setProbability(1.);
                            faultEventRepositoryService.persist(faultEventChild);
                        }
                        faultEvent.addChild(faultEventChild);
                        processBehavior(behaviorChild, faultEventChild);
                    }
                }
                faultEventRepositoryService.persist(faultEvent);
            }
        }
        return faultEvent;
    }

    private void processChildBehaviors(Behavior behavior,FaultEvent parentFaultEvent) throws URISyntaxException {
        FaultEvent faultEvent;
        if(behavior instanceof Function){
            URI faultEventUri = createUri(behavior, null, "f");
            if (faultEventRepositoryService.existsInPersistenceContext(faultEventUri)) {
                faultEvent = faultEventRepositoryService.findRequired(faultEventUri);
            } else {
                faultEvent = FaultEvent.create();
                faultEvent.setBehavior(behavior);
                faultEvent.setName(behavior.getName() + " fails b/c its parts fail");
                faultEvent.setEventType(FtaEventType.INTERMEDIATE);
                faultEvent.setGateType(behavior.getBehaviorType() == BehaviorType.AndBehavior ? GateType.OR : GateType.AND);
                faultEvent.setUri(faultEventUri);
                faultEventRepositoryService.persist(faultEvent);
            }
            parentFaultEvent.addChild(faultEvent);

            for (Behavior behaviorChild : behavior.getChildBehaviors()) {
                if(isVisited(behaviorChild))
                    continue;
                FaultEvent fEvent = FaultEvent.create();
                faultEventUri = createUri(behaviorChild, faultEvent, "");
                if (faultEventRepositoryService.existsInPersistenceContext(faultEventUri)) {
                    fEvent = faultEventRepositoryService.findRequired(faultEventUri);
                } else {
                    fEvent.setBehavior(behaviorChild);
                    fEvent.setName(behavior.getName() + " fails b/c " + behaviorChild.getName() + " fails");
                    fEvent.setEventType(FtaEventType.INTERMEDIATE);
                    fEvent.setGateType(GateType.OR);
                    fEvent.setUri(faultEventUri);
                    faultEventRepositoryService.persist(fEvent);
                }
                faultEvent.addChild(fEvent);
                processBehavior(behaviorChild, fEvent);
            }
        }
    }

    protected void initTreeGeneration(){
        visitedBehaviors.set(new HashSet<>());
    }
    protected void cleanTreeGeneration(){
        visitedBehaviors.set(null);
    }

    protected boolean isVisited(Behavior b){
        Set<Behavior> visited = visitedBehaviors.get();
        return visited.contains(b);
    }

    protected boolean addVisited(Behavior b){
        Set<Behavior> visited = visitedBehaviors.get();
        return visited.add(b);
    }

    protected boolean removeVisited(Behavior b){
        Set<Behavior> visited = visitedBehaviors.get();
        return visited.remove(b);
    }

    private void setFaultEventTypes(Behavior behaviorChild, FaultEvent fEvent) {
        boolean isBasic = behaviorChild.getChildBehaviors().isEmpty()
                && behaviorChild.getRequiredBehaviors().isEmpty()
                && functionRepositoryService.getImpairingBehaviors(behaviorChild.getUri()).isEmpty();

        setFaultEventTypes(isBasic, fEvent);
    }

    private void setFaultEventTypes(boolean isBasic, FaultEvent fEvent){
        if(isBasic){
            fEvent.setEventType(FtaEventType.BASIC);
            fEvent.setGateType(null);
            fEvent.setProbability(1.);
        }else{
            fEvent.setEventType(FtaEventType.INTERMEDIATE);
            fEvent.setGateType(GateType.OR);
        }
    }

    @Transactional(readOnly = true)
    public List<FaultTree> findAllSummaries(){
        return ((FaultTreeDao)getPrimaryDao()).findAllSummaries();
    }

    @Transactional
    public FaultTree performCutSetAnalysis(URI faultTreeUri){
        FaultTree faultTree = findRequired(faultTreeUri);
        CutSetExtractor extractor = new CutSetExtractor();
        List<FaultEventScenario> scenarios = extractor.extractMinimalScenarios(faultTree);

        if(faultTree.getFaultEventScenarios() != null)
            for(FaultEventScenario faultEventScenario : faultTree.getFaultEventScenarios())
                faultEventScenarioDao.remove(faultEventScenario);

        for(FaultEventScenario scenario : scenarios){
            scenario.updateProbability();
        }
        faultTree.setFaultEventScenarios(new HashSet<>(scenarios));
        getPrimaryDao().update(faultTree);

        return faultTree;
    }

    @Transactional
    public FaultTree evaluate(URI faultTreeUri) {
        FaultTree faultTree = findRequired(faultTreeUri);

        if(faultTree.getFaultEventScenarios() != null) {
            for (FaultEventScenario faultEventScenario : faultTree.getFaultEventScenarios())
                faultEventScenarioDao.remove(faultEventScenario);
            faultTree.setFaultEventScenarios(null);
        }

        FTAMinimalCutSetEvaluation evaluator = new FTAMinimalCutSetEvaluation();
        evaluator.evaluate(faultTree);

        if(faultTree.getFaultEventScenarios() != null) {
            for (FaultEventScenario scenario : faultTree.getFaultEventScenarios()) {
                scenario.updateProbability();
            }
            getPrimaryDao().update(faultTree);
        }
        return faultTree;
    }
}
