package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FaultEventDao;
import cz.cvut.kbss.analysis.dao.FaultEventTypeDao;
import cz.cvut.kbss.analysis.dao.FaultTreeDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.exception.LogicViolationException;
import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.model.diagram.Rectangle;
import cz.cvut.kbss.analysis.model.fta.FtaEventType;
import cz.cvut.kbss.analysis.service.strategy.DirectFtaEvaluation;
import cz.cvut.kbss.analysis.util.Vocabulary;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

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

    @Autowired
    public FaultEventRepositoryService(@Qualifier("faultEventValidator") Validator validator, FaultEventDao faultEventDao, ComponentRepositoryService componentRepositoryService, FaultTreeDao faultTreeDao, FaultEventTypeService faultEventTypeService, FaultEventTypeDao faultEventTypeDao) {
        super(validator);
        this.faultEventDao = faultEventDao;
        this.componentRepositoryService = componentRepositoryService;
        this.faultTreeDao = faultTreeDao;
        this.faultEventTypeService = faultEventTypeService;
        this.faultEventTypeDao = faultEventTypeDao;
    }

    @Override
    protected GenericDao<FaultEvent> getPrimaryDao() {
        return faultEventDao;
    }

    @Override
    protected void preRemove(FaultEvent instance) {
        boolean isRootEvent = faultTreeDao.isRootEvent(instance.getUri());
        if (isRootEvent) throw new LogicViolationException("Root event of tree mustn't be deleted!");
    }

    @Transactional
    public FaultEvent addInputEvent(URI eventUri, FaultEvent inputEvent) {
        FaultEvent currentEvent = findRequired(eventUri);

        if(inputEvent.getUri() == null && inputEvent.getRectangle() == null)
            inputEvent.setRectangle(new Rectangle());

        faultEventTypeService.loadManagedSupertypes(inputEvent);
        currentEvent.addChild(inputEvent);
        update(currentEvent);

        // TODO optimize? Two phase 'update' is necessary because 'inputEvent' is persisted and has no URI yet.
        currentEvent.addChildSequenceUri(inputEvent.getUri());
        update(currentEvent);

        setExternalReference(eventUri, inputEvent);
        return inputEvent;
    }

    protected void setExternalReference(URI eventUri, FaultEvent inputEvent){
        if(inputEvent.getSupertypes() == null || inputEvent.getEventType() != FtaEventType.EXTERNAL)
            return;

        List<Event> supertypes = inputEvent.getSupertypes().stream()
                .filter(e -> e.getTypes() == null || !e.getTypes().contains(ATOMIC_TYPE))
                .toList();
        if(supertypes.isEmpty())
            return;

        inputEvent.setIsReference(true);

        if(supertypes.size() > 1)
            log.warn("new event added to event <{}> has multiple supertypes [{}]",
                    eventUri,
                    supertypes.stream().map(e -> String.format("<%s>", e.getUri().toString()))
                            .collect(Collectors.joining(",")));

        Event supertype = supertypes.get(0);
        List<FaultEventReference> referencedRoots = faultEventTypeDao.getFaultEventRootWithSupertype(supertype.getUri());

        if(referencedRoots == null || referencedRoots.isEmpty())
            return;

        if(referencedRoots.size() > 1)
            log.warn("new event added to event <{}> with supertype <{}> is used in multiple root fault events [{}]",
                    eventUri, supertype.getUri(),
                    referencedRoots.stream().map(u -> String.format("<%s>", u.toString()))
                            .collect(Collectors.joining(",")));

        inputEvent.setReferences(referencedRoots.get(0));
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
        setChange(instance);
    }

    protected void setChange(FaultEvent instance){
        URI context = faultEventDao.getContext(instance);
        faultTreeDao.setChangedByContext(context, new Date());
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
