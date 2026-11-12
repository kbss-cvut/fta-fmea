package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FaultEventDao;
import cz.cvut.kbss.analysis.dao.FaultEventTypeDao;
import cz.cvut.kbss.analysis.dao.FaultTreeDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.exception.LogicViolationException;
import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.model.diagram.Rectangle;
import cz.cvut.kbss.analysis.model.fta.FtaEventType;
import cz.cvut.kbss.analysis.service.security.SecurityUtils;
import cz.cvut.kbss.analysis.service.strategy.DirectFtaEvaluation;
import cz.cvut.kbss.analysis.service.validation.EntityValidator;
import cz.cvut.kbss.analysis.util.Vocabulary;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FaultEventRepositoryService extends BaseRepositoryService<FaultEvent> {

    public static final URI ATOMIC_TYPE = URI.create(Vocabulary.s_c_atomic_event_type);
    public static final URI COMPLEX_TYPE = URI.create(Vocabulary.s_c_complex_event_type);


    private final FaultEventDao faultEventDao;
    private final ComponentRepositoryService componentRepositoryService;
    private final FaultTreeDao faultTreeDao;
    private final FaultEventTypeService faultEventTypeService;
    private final FaultEventTypeDao faultEventTypeDao;
    private final SecurityUtils securityUtils;

    @Autowired
    public FaultEventRepositoryService(@Qualifier("faultEventValidator") EntityValidator validator, FaultEventDao faultEventDao, ComponentRepositoryService componentRepositoryService, FaultTreeDao faultTreeDao, FaultEventTypeService faultEventTypeService, FaultEventTypeDao faultEventTypeDao, SecurityUtils securityUtils) {
        super(validator);
        this.faultEventDao = faultEventDao;
        this.componentRepositoryService = componentRepositoryService;
        this.faultTreeDao = faultTreeDao;
        this.faultEventTypeService = faultEventTypeService;
        this.faultEventTypeDao = faultEventTypeDao;
        this.securityUtils = securityUtils;
    }

    @Override
    protected GenericDao<FaultEvent> getPrimaryDao() {
        return faultEventDao;
    }

    @Override
    protected void preRemove(FaultEvent instance) {
        boolean isRootEvent = faultTreeDao.isRootEvent(instance.getUri());
        if (isRootEvent) throw new LogicViolationException("error.faultTree.rootEvent.deleteViolation","Root event of tree mustn't be deleted!");
    }



    /**
     * Copies nodesToPast and each of the nodes subtrees to the target node. If the
     * @param targetNodeUri
     * @param nodesToPaste
     * @return
     */
    @Transactional
    public List<FaultEvent> copyEvents(URI targetNodeUri, List<URI> nodesToPaste) {
        List<URI> actualNodeUrisToPaste = nodesToPaste;
        List<URI> sequence = nodesToPaste;


        // find root node
        URI treeRootNodeUri = nodesToPaste.stream()
                .filter(u -> !faultEventDao.isChild(u))
                .findFirst().orElse(null);


        // If tree root is selected to be pasted, copy its child nodes instead.
        if (treeRootNodeUri != null) {
            FaultEvent treeRootNode = find(treeRootNodeUri).orElse(null);
            if (treeRootNode != null && treeRootNode.getChildren() != null) {
                sequence = partsInSequence(treeRootNode).stream().map(FaultEvent::getUri).toList();
                actualNodeUrisToPaste = new ArrayList<>(sequence);
                Set<URI> inSequence = new HashSet<>(sequence);
                List<URI> notInSequence = treeRootNode.getChildren().stream().map(FaultEvent::getUri)
                        .filter(u -> !inSequence.contains(u))
                        .sorted(Comparator.comparing(URI::toString)).toList();
                actualNodeUrisToPaste.addAll(notInSequence);
            }
        }

        if (actualNodeUrisToPaste == null || actualNodeUrisToPaste.isEmpty())
            return null;


        List<FaultEvent> toPaste = actualNodeUrisToPaste.stream()
//                .filter(faultEventDao::isChild) // filter out the fault tree root event if present
                .map(u -> find(u).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        return toPaste;
    }

    @Transactional
    public List<URI> pasteEvents(URI targetNodeUri, List<FaultEvent> toPaste) {
        List<URI> actualNodeUrisToPaste = toPaste.stream().map(AbstractEntity::getUri).toList();
        FaultEvent targetNode = findRequired(targetNodeUri);
        if(targetNode.getEventType() != FtaEventType.INTERMEDIATE)
            throw new LogicViolationException("error.faultTree.paste", "Paste is allowed only in intermediate node.");
        // detach, clear URIs and child sequence
        Set<URI> processed = new HashSet<>();
        Stack<FaultEvent> stack = new Stack<>();
        stack.addAll(toPaste);
        Map<FaultEvent, List<FaultEvent>> partSequences = new HashMap<>();
        while(!stack.isEmpty()){
            FaultEvent fe = stack.pop();
            if(!processed.add(fe.getUri()))
                continue;
//            faultEventDao.detach(fe);
            fe.setUri(null);
            fe.getRectangle().setUri(null);

            if(fe.getChildren() != null && !fe.getChildren().isEmpty())
                fe.getChildren().forEach(stack::push);

            if(fe.getChildrenSequence() != null && !fe.getChildrenSequence().isEmpty()) {
                List<FaultEvent> partSequence = partsInSequence(fe);
                partSequences.put(fe, partSequence);
            }
        }

        // past roots (and their subtrees) into taget nodes
        if(targetNode.getChildren() == null)
            targetNode.setChildren(new HashSet<>());
        targetNode.getChildren().addAll(toPaste);
        if(targetNode.getChildrenSequence() == null)
            targetNode.setChildrenSequence(new ArrayList<>());
        targetNode.getChildrenSequence().addAll(actualNodeUrisToPaste);

        faultEventDao.update(targetNode);

        // restore sequences in child nodes indirect to targetNode
        partSequences.entrySet().stream().forEach(e -> e.getKey().setChildrenSequence(e.getValue().stream()
                .map(FaultEvent::getUri).toList()));
        return actualNodeUrisToPaste;
    }

    protected List<FaultEvent> partsInSequence(FaultEvent fe){
        Map<URI, FaultEvent> partMap = new HashMap<>();
        fe.getChildren().forEach(p -> partMap.put(p.getUri(), p));
        List<FaultEvent> sequence = new ArrayList<>();
        fe.getChildrenSequence().stream()
                .map(partMap::remove)
                .filter(Objects::nonNull)
                .forEach( sequence::add);
//        // add all parts which were not in sequence at the end of the sequence in lexicographical order by their URIs.
//        partMap.values().stream()
//                .sorted(Comparator.comparing(f -> f.getUri().toString()))
//                .forEach(sequence::add);
        return sequence;
    }

    @Transactional
    public FaultEvent addInputEvent(URI eventUri, FaultEvent inputEvent) {
        inputEvent.setProbabilityUpdated(true);
        validateNew(inputEvent);
        FaultEvent currentEvent = findRequired(eventUri);

        if(inputEvent.getUri() == null && inputEvent.getRectangle() == null)
            inputEvent.setRectangle(new Rectangle());

        faultEventTypeService.loadManagedSupertypes(inputEvent);
        currentEvent.addChild(inputEvent);
        update(currentEvent);

        // TODO optimize? Two phase 'update' is necessary because 'inputEvent' is persisted and has no URI yet.
        currentEvent.addChildSequenceUri(inputEvent.getUri());
        update(currentEvent);

        setExternalReference(inputEvent);
        if(inputEvent.getReferences() != null)
            inputEvent.setProbability(inputEvent.getReferences().getProbability());
        return inputEvent;
    }

    public void setExternalReference(FaultEvent inputEvent){
        if(inputEvent.getSupertypes() == null || inputEvent.getEventType() != FtaEventType.EXTERNAL)
            return;

        List<Event> supertypes = inputEvent.getSupertypes().stream()
                .filter(e -> e.getTypes() == null || !e.getTypes().contains(ATOMIC_TYPE))
                .toList();
        if(supertypes.isEmpty())
            return;

        inputEvent.setIsReference(true);

        if(supertypes.size() > 1)
            log.warn("event \"{}\"<{}> , has multiple supertypes [{}]",
                    inputEvent.getName(), inputEvent.getUri(),
                    supertypes.stream().map(e -> String.format("<%s>", e.getUri().toString()))
                            .collect(Collectors.joining(",")));

        Event supertype = supertypes.get(0);
        List<FaultEventReference> referencedRoots = faultEventTypeDao.getFaultEventRootWithSupertype(supertype.getUri());

        if(referencedRoots == null || referencedRoots.isEmpty())
            return;

        if(referencedRoots.size() > 1)
            log.warn("event \"{}\"<{}> with supertype <{}> is used in multiple root fault events [{}]",
                    inputEvent.getName(), inputEvent.getUri(), supertype.getUri(),
                    referencedRoots.stream().map(u -> String.format("<%s>", u.toString()))
                            .collect(Collectors.joining(",")));

        inputEvent.setReferences(referencedRoots.get(0));
    }

    @Transactional
    public void updateProbabilityFromReferencedNode(FaultEvent faultEvent, URI faultTreeUri){
        if(faultEvent.getReferences() == null || faultEvent.getReferences().getProbability() == null)
            return;
        faultEventDao.setProbability(faultEvent.getUri(), faultEvent.getReferences().getProbability(), faultTreeUri);
        faultEvent.setProbability(faultEvent.getReferences().getProbability());
    }

    @Transactional(readOnly = true)
    public Double propagateProbability(FaultEvent event) {
        log.info("> propagateProbability - {}", event);
        Double resultProbability = new DirectFtaEvaluation().evaluate(event);
        log.info("< propagateProbability - {}", resultProbability);
        return resultProbability;
    }

    @Transactional(readOnly = true)
    public FailureMode getFailureMode(URI faultEventUri) {
        log.info("> getFailureMode - {}", faultEventUri);

        FailureMode failureMode = findRequired(faultEventUri).getFailureMode();
        log.info("< getFailureMode - {}", failureMode);
        return failureMode;
    }

    @Transactional
    public FailureMode addFailureMode(URI faultEventUri, FailureMode failureMode) {
        log.info("> addFailureMode - {}, {}", faultEventUri, failureMode);

        FaultEvent event = findRequired(faultEventUri);
        event.setBehavior(failureMode);

        Item item = componentRepositoryService.findRequired(failureMode.getItem().getUri());
        item.addFailureMode(failureMode);

        update(event);

        log.info("< addFailureMode - {}", failureMode);
        return failureMode;
    }

    @Transactional
    public void deleteFailureMode(URI faultEventUri) {
        log.info("> deleteFailureMode - {}", faultEventUri);

        FaultEvent event = findRequired(faultEventUri);
        event.getFailureMode()
                .getManifestations()
                .removeIf(e -> e.getUri().equals(faultEventUri));
        event.setFailureMode(null);

        update(event);

        log.info("< deleteFailureMode");
    }

    @Transactional
    public void updateChildrenSequence(URI faultEventUri, List<URI> childrenSequence) {
        log.info("> updateChildrenSequence - {}, {}", faultEventUri, childrenSequence);

        FaultEvent faultEvent = findRequired(faultEventUri);
        faultEvent.setChildrenSequence(childrenSequence);
        update(faultEvent);

        log.info("< updateChildrenSequence");
    }

    @Transactional
    @Override
    public FaultEvent update(FaultEvent instance) {
        Objects.requireNonNull(instance);
        preUpdate(instance);
        FaultEvent managedInstance = findRequired(instance.getUri());
        managedInstance.setName(instance.getName());
        managedInstance.setDescription(instance.getDescription());
        managedInstance.setGateType(instance.getGateType());
        managedInstance.setEventType(instance.getEventType());
        if(instance.getProbability() != managedInstance.getProbability())
            managedInstance.setProbabilityUpdated(true);
        managedInstance.setProbability(instance.getProbability());
        managedInstance.setSupertypes(instance.getSupertypes());
        managedInstance.setChildrenSequence(instance.getChildrenSequence());
        managedInstance.setSelectedEstimate(instance.getSelectedEstimate());
        faultEventDao.getContext(managedInstance);
        faultEventDao.update(managedInstance);
        postUpdate(managedInstance);
        return managedInstance;
    }

    @Override
    protected void preUpdate(FaultEvent instance) {
        if(instance.getSupertypes() != null && !instance.getSupertypes().isEmpty())
            faultEventTypeService.loadManagedSupertypes(instance);

        super.preUpdate(instance);
    }

    @Transactional
    public void update(Rectangle rect){
        faultEventDao.update(rect);
        log.trace("< updateRectangle");
    }

    @Transactional(readOnly = true)
    public boolean isRootEventReused(FaultEvent rootEvent) {
        return faultEventDao.isChild(rootEvent.getUri());
    }

    @Override
    protected void postUpdate(@NonNull FaultEvent instance) {
        super.postUpdate(instance);
        setChange(instance);
    }

    @Override
    protected void postRemove(@NonNull FaultEvent instance) {
        super.postRemove(instance);
        instance.setProbabilityUpdated(true);
        setChange(instance);
    }

    protected void setChange(FaultEvent instance){
        URI context = faultEventDao.getContext(instance);
        UserReference userReference = securityUtils.getCurrentUserReference();
        if(instance.isProbabilityUpdated())
            faultTreeDao.updateStatus(context, Status.outOfSync);
        faultTreeDao.setChangedByContext(context, new Date(), userReference.getUri());
    }

    public List<FaultEventType> getTopFaultEvents(URI systemUri) {
        return faultEventTypeDao.getTopFaultEvents(systemUri);
    }

    public List<FaultEventType> getAllFaultEvents(URI faultTreeUri) {
        FaultTree ftSummary = faultTreeDao.findSummary(faultTreeUri);
        List<FaultEventType> ret = faultEventTypeDao.getAllFaultEvents(ftSummary.getSystem().getUri());
        Set<URI> typesToRemove = Optional.ofNullable(ftSummary.getManifestingEvent()).map(r -> r.getSupertypes())
                .filter(s -> s !=null && !s.isEmpty())
                .map(s -> s.stream().map(t -> t.getUri()).collect(Collectors.toSet()))
                .orElse(null);
        return typesToRemove != null
                ? ret.stream().filter(t -> !typesToRemove.contains(t.getUri())).toList()
                : ret;
    }

    public FaultEventType getFaultEventSupertype(URI faultEventUri) {
        return faultEventTypeDao.getFaultEventSupertype(faultEventUri);
    }
}
