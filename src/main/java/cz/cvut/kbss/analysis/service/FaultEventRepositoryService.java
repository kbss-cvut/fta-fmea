package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.ComponentDao;
import cz.cvut.kbss.analysis.dao.FaultEventDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.util.EventType;
import cz.cvut.kbss.analysis.service.strategy.GateStrategyFactory;
import cz.cvut.kbss.analysis.service.validation.FaultEventValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FaultEventRepositoryService {

    private final FaultEventDao faultEventDao;
    private final FaultEventValidator faultEventValidator;

    private final ComponentRepositoryService componentRepositoryService;

    @Transactional(readOnly = true)
    public List<FaultEvent> findFaultEvents() {
        return faultEventDao.findAll();
    }

    @Transactional
    public void delete(URI eventUri) {
        faultEventDao.remove(eventUri);
    }

    @Transactional
    public void updateEvent(FaultEvent event) {
        faultEventValidator.validateTypes(event);

        faultEventDao.update(event);
    }

    @Transactional
    public FaultEvent addInputEvent(URI eventUri, FaultEvent inputEvent) {
        FaultEvent currentEvent = getEvent(eventUri);

        if (inputEvent.getUri() == null) {
            faultEventValidator.validateDuplicates(inputEvent);
            faultEventValidator.validateTypes(inputEvent);
        } else {
            log.info("Using existing event - {}", inputEvent);
        }

        currentEvent.addChild(inputEvent);
        faultEventDao.update(currentEvent);

        return inputEvent;
    }

    @Transactional(readOnly = true)
    public Double propagateProbability(FaultEvent event) {
        log.info("> propagateProbability - {}", event);

        if (event.getEventType() == EventType.INTERMEDIATE) {
            List<Double> childProbabilities = event.getChildren().stream()
                    .map(this::propagateProbability).collect(Collectors.toList());

            double eventProbability = GateStrategyFactory.get(event.getGateType()).propagate(childProbabilities);
            event.setProbability(eventProbability);
        }

        Double resultProbability = event.getProbability();

        log.info("< propagateProbability - {}", resultProbability);
        return resultProbability;
    }

    public FailureMode getFailureMode(URI faultEventUri) {
        log.info("> getFailureMode - {}", faultEventUri);

        FailureMode failureMode = getEvent(faultEventUri).getFailureMode();
        log.info("< getFailureMode - {}", failureMode);
        return failureMode;
    }

    @Transactional
    public FailureMode addFailureMode(URI faultEventUri, FailureMode failureMode) {
        log.info("> addFailureMode - {}, {}", faultEventUri, failureMode);

        FaultEvent event = getEvent(faultEventUri);
        failureMode.addEffect(event);

        Component component = componentRepositoryService.getComponent(failureMode.getComponent().getUri());
        component.addFailureMode(failureMode);

        faultEventDao.update(event);

        log.info("< addFailureMode - {}", failureMode);
        return failureMode;
    }

    @Transactional
    public void deleteFailureMode(URI faultEventUri) {
        log.info("> deleteFailureMode - {}", faultEventUri);

        FaultEvent event = getEvent(faultEventUri);
        event.getFailureMode()
                .getEffects()
                .removeIf(e -> e.getUri().equals(faultEventUri));
        event.setFailureMode(null);

        faultEventDao.update(event);

        log.info("< deleteFailureMode");
    }

    private FaultEvent getEvent(URI eventUri) {
        return faultEventDao
                .find(eventUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find fault event"));
    }
}
