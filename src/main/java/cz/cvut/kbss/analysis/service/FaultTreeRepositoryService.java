package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FaultTreeDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.model.util.EventType;
import cz.cvut.kbss.analysis.model.util.GateType;
import cz.cvut.kbss.analysis.service.util.FaultTreeTraversalUtils;
import cz.cvut.kbss.analysis.util.Vocabulary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FaultTreeRepositoryService extends BaseRepositoryService<FaultTree> {

    private final FaultTreeDao faultTreeDao;
    private final FaultEventRepositoryService faultEventRepositoryService;
    private final FunctionRepositoryService functionRepositoryService;
    private final FailureModeRepositoryService failureModeRepositoryService;
    private final IdentifierService identifierService;

    @Autowired
    public FaultTreeRepositoryService(@Qualifier("defaultValidator") Validator validator,
                                      FaultTreeDao faultTreeDao,
                                      FaultEventRepositoryService faultEventRepositoryService,
                                      FunctionRepositoryService functionRepositoryService,
                                      FailureModeRepositoryService failureModeRepositoryService,
                                      IdentifierService identifierService
    ) {
        super(validator);
        this.faultTreeDao = faultTreeDao;
        this.faultEventRepositoryService = faultEventRepositoryService;
        this.functionRepositoryService = functionRepositoryService;
        this.failureModeRepositoryService = failureModeRepositoryService;
        this.identifierService = identifierService;
    }

    @Override
    protected GenericDao<FaultTree> getPrimaryDao() {
        return faultTreeDao;
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

    @Override
    protected void preUpdate(FaultTree instance) {
        super.preUpdate(instance);
        propagateProbabilities(instance);
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
        Function function = functionRepositoryService.findRequired(functionUri);
        FaultEvent faultEvent;

        faultEvent = transferBehaviorToFaultEvent(function,null);
        FaultTree faultTree = new FaultTree();
        faultTree.setName(faultTreeName);
        faultTree.setManifestingEvent(faultEvent);

        processBehavior(function, faultEvent);
        faultEvent.setEventType(EventType.INTERMEDIATE);
        persist(faultTree);
        return faultTree;
    }

    private void processBehavior(Behavior behavior, FaultEvent parentFaultEvent) throws URISyntaxException {
        Set<FaultEvent> faultEvents = new LinkedHashSet<>();
        List<Behavior> impairedBehaviors = functionRepositoryService.getImpairedBehaviors(behavior.getUri());

        if(!behavior.getChildBehaviors().isEmpty() ){
            processChildBehaviors(behavior, parentFaultEvent);
        }

        for (Behavior requiredBehavior : behavior.getRequiredBehaviors()) {
            FaultEvent tmp = transferBehaviorToFaultEvent(requiredBehavior, behavior);
            faultEvents.add(tmp);
            processBehavior(requiredBehavior, tmp);
        }

        if (!impairedBehaviors.isEmpty()) {
            for (Behavior impairedBehavior : impairedBehaviors) {
                faultEvents.add(processImpairedBehavior(impairedBehavior,behavior));
            }
        }else{
            if (behavior instanceof Function) {
                FailureMode failureMode = new FailureMode();
                failureMode.setName(behavior.getName() + "-fm");
                failureMode.setComponent(functionRepositoryService.getComponent(behavior.getUri()));
                failureMode.addImpairedBehavior(behavior);
                failureModeRepositoryService.persist(failureMode);

                FaultEvent faultEvent = transferBehaviorToFaultEvent(failureMode, behavior);
                faultEvent.setFailureMode(failureMode);
                faultEvent.setEventType(EventType.BASIC);
                faultEvent.setGateType(GateType.UNUSED);
                faultEvent.setProbability(1.);
                faultEvents.add(faultEvent);
                faultEventRepositoryService.persist(faultEvent);
            }
        }
        parentFaultEvent.addChildren(faultEvents);
    }

    private FaultEvent transferBehaviorToFaultEvent(Behavior behavior, Behavior parentBehavior) throws URISyntaxException {
        URI faultEventUri = createUri(behavior, parentBehavior, "");
        URI faultEventUri1 = createUri(behavior, parentBehavior, "e");
        URI faultEventUri2 = createUri(behavior, parentBehavior, "f");

        if (faultEventRepositoryService.exists(faultEventUri)) {
            return faultEventRepositoryService.findRequired(faultEventUri);
        } else if(faultEventRepositoryService.exists(faultEventUri1)){
            return faultEventRepositoryService.findRequired(faultEventUri1);
        } else if(faultEventRepositoryService.exists(faultEventUri2)){
            return faultEventRepositoryService.findRequired(faultEventUri2);
        } else {
            FaultEvent faultEvent = new FaultEvent();
            faultEvent.setUri(faultEventUri);
            faultEvent.setBehavior(behavior);
            if (behavior instanceof Function) {
                faultEvent.setName(behavior.getName() + " fails");
                faultEvent.setEventType(EventType.INTERMEDIATE);
                faultEvent.setGateType(GateType.OR);
            } else if (behavior instanceof FailureMode) {
                faultEvent.setName(parentBehavior.getName() + " fails as " + behavior.getName());
                faultEvent.setEventType(EventType.BASIC);
                faultEvent.setGateType(GateType.UNUSED);
                faultEvent.setProbability(1.);
            }
            faultEventRepositoryService.persist(faultEvent);
            return faultEvent;
        }
    }

    private URI createUri(Behavior behavior, Behavior parentBehavior, String type) throws URISyntaxException {
        String behaviorUri = behavior.getUri().toString();
        if(parentBehavior == null){
            return new URI(identifierService.composeIdentifier(Vocabulary.s_c_FaultEvent
                    , behaviorUri.substring(behaviorUri.lastIndexOf("/") + 1)) + type);
        }else{
            String parentBehaviorUri = parentBehavior.getUri().toString();
            return new URI(identifierService.composeIdentifier(Vocabulary.s_c_FaultEvent
                    , behaviorUri.substring(behaviorUri.lastIndexOf("/") + 1)) + parentBehaviorUri.split("instance")[1] + type);
        }
    }

    private FaultEvent processImpairedBehavior(Behavior behavior, Behavior parentBehavior) throws URISyntaxException {
        FaultEvent faultEvent;
        if(behavior.getBehaviorType() == BehaviorType.AtomicBehavior && parentBehavior instanceof Function) {
            faultEvent = transferBehaviorToFaultEvent(behavior, parentBehavior);
        }else{
            URI faultEventUri = createUri(behavior, parentBehavior, "");
            URI faultEventUriTypeEvent = createUri(behavior, parentBehavior, "e");

            if(faultEventRepositoryService.exists(faultEventUri)) {
                faultEvent = faultEventRepositoryService.findRequired(faultEventUri);
            }else if(faultEventRepositoryService.exists(faultEventUriTypeEvent)){
                faultEvent = faultEventRepositoryService.findRequired(faultEventUriTypeEvent);
            }else {
                faultEvent = new FaultEvent();
                faultEvent.setUri(faultEventUri);
                faultEvent.setBehavior(behavior);
                if(behavior.getBehaviorType() == BehaviorType.AtomicBehavior){
                    faultEventUri = createUri(behavior, parentBehavior, "e");
                    faultEvent.setUri(faultEventUri);
                    faultEvent.setName(behavior.getName() + " event");
                    faultEvent.setEventType(EventType.BASIC);
                    faultEvent.setGateType(GateType.UNUSED);
                    faultEvent.setProbability(1.);
                }else{
                    faultEventUri = createUri(behavior, parentBehavior, "");
                    faultEvent.setUri(faultEventUri);
                    faultEvent.setName(parentBehavior.getName() + " fails as " + behavior.getName());
                    faultEvent.setEventType(EventType.INTERMEDIATE);
                    faultEvent.setGateType(behavior.getBehaviorType() == BehaviorType.OrBehavior ? GateType.OR : GateType.AND);

                    for (Behavior behaviorChild : behavior.getChildBehaviors()) {
                        FaultEvent faultEventChild = new FaultEvent();
                        faultEventUri = createUri(behaviorChild, behavior, "e");
                        if (faultEventRepositoryService.exists(faultEventUri)) {
                            faultEventChild = faultEventRepositoryService.findRequired(faultEventUri);
                        } else {
                            faultEventUri = createUri(behaviorChild, behavior, "e");
                            faultEventChild.setUri(faultEventUri);
                            faultEventChild.setName(behaviorChild.getName() + " event");
                            faultEventChild.setEventType(EventType.BASIC);
                            faultEventChild.setGateType(GateType.UNUSED);
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
            if (faultEventRepositoryService.exists(faultEventUri)) {
                faultEvent = faultEventRepositoryService.findRequired(faultEventUri);
            } else {
                faultEvent = new FaultEvent();
                faultEvent.setBehavior(behavior);
                faultEvent.setName(behavior.getName() + " fails b/c its parts fail");
                faultEvent.setEventType(EventType.INTERMEDIATE);
                faultEvent.setGateType(behavior.getBehaviorType() == BehaviorType.AndBehavior ? GateType.OR : GateType.AND);
                faultEvent.setUri(faultEventUri);
                faultEventRepositoryService.persist(faultEvent);
            }
            parentFaultEvent.addChild(faultEvent);

            for (Behavior behaviorChild : behavior.getChildBehaviors()) {
                FaultEvent fEvent = new FaultEvent();
                faultEventUri = createUri(behaviorChild, behavior, "");
                if (faultEventRepositoryService.exists(faultEventUri)) {
                    fEvent = faultEventRepositoryService.findRequired(faultEventUri);
                } else {
                    fEvent.setBehavior(behaviorChild);
                    fEvent.setName(behavior.getName() + " fails b/c " + behaviorChild.getName() + " fails");
                    fEvent.setEventType(EventType.INTERMEDIATE);
                    fEvent.setGateType(GateType.OR);
                    fEvent.setUri(faultEventUri);
                    faultEventRepositoryService.persist(fEvent);
                }
                faultEvent.addChild(fEvent);
                processBehavior(behaviorChild, fEvent);
            }
        }

    }
}
