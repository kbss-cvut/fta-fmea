package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FaultEventDao;
import cz.cvut.kbss.analysis.dao.FaultTreeDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.exception.LogicViolationException;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.Item;
import cz.cvut.kbss.analysis.model.diagram.Rectangle;
import cz.cvut.kbss.analysis.service.strategy.DirectFtaEvaluation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
public class FaultEventRepositoryService extends BaseRepositoryService<FaultEvent> {

    private final FaultEventDao faultEventDao;
    private final ComponentRepositoryService componentRepositoryService;
    private final FaultTreeDao faultTreeDao;

    @Autowired
    public FaultEventRepositoryService(@Qualifier("faultEventValidator") Validator validator, FaultEventDao faultEventDao, ComponentRepositoryService componentRepositoryService, FaultTreeDao faultTreeDao) {
        super(validator);
        this.faultEventDao = faultEventDao;
        this.componentRepositoryService = componentRepositoryService;
        this.faultTreeDao = faultTreeDao;
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

        currentEvent.addChild(inputEvent);
        update(currentEvent);

        // TODO optimize? Two phase 'update' is necessary because 'inputEvent' is persisted and has no URI yet.
        currentEvent.addChildSequenceUri(inputEvent.getUri());
        update(currentEvent);

        return inputEvent;
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
    public void update(Rectangle rect){
        faultEventDao.update(rect);
        log.trace("< updateRectangle");
    }

    @Transactional(readOnly = true)
    public boolean isRootEventReused(FaultEvent rootEvent) {
        return faultEventDao.isChild(rootEvent.getUri());
    }

}
