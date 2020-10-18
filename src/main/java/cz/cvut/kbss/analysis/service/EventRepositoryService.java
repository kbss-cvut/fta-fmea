package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FaultEventDao;
import cz.cvut.kbss.analysis.dao.GateDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.exception.InvalidEntityTypeException;
import cz.cvut.kbss.analysis.exception.LogicViolationException;
import cz.cvut.kbss.analysis.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventRepositoryService {

    private final FaultEventDao faultEventDao;
    private final GateDao gateDao;

    // TODO refactor usage of both URIs ?
    @Transactional
    public URI addInputEvent(URI gateUri, URI eventUri, FaultEvent inputEvent) {
        Gate gate = gateDao
                .find(gateUri)
                .orElseGet(() -> {
                    log.info("Could not find gate {}. Searching for event instead.", gateUri);
                    FaultEvent faultEvent = faultEventDao
                            .find(eventUri)
                            .orElseThrow(() -> new EntityNotFoundException("Failed to find failure mode"));

                    Gate intermediateGate = faultEvent.getInputGate();
                    if (intermediateGate == null) {
                        log.info("Creating intermediate gate under {}", faultEvent);
                        intermediateGate = new Gate();
                        faultEvent.setInputGate(intermediateGate);
                        intermediateGate.setProducedEvent(faultEvent);
                        faultEventDao.update(faultEvent);
                    }

                    return intermediateGate;
                });

        gate.addInputEvent(inputEvent);
        inputEvent.setEnteredGate(gate);
        gateDao.update(gate);

        return inputEvent.getUri();
    }

    @Transactional(readOnly = true)
    public Set<FaultEvent> getInputEvents(URI eventUri) {
        Gate gate = gateDao
                .find(eventUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find failure mode"));

        return gate.getInputEvents();
    }

    @Transactional
    public URI setTakenAction(URI eventUri, TakenAction takenAction) {
        FaultEvent faultEvent = faultEventDao
                .find(eventUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find failure mode"));

        faultEvent.setTakenAction(takenAction);
        takenAction.setFaultEvent(faultEvent);

        faultEventDao.update(faultEvent);
        return takenAction.getUri();
    }

    @Transactional
    public URI insertGate(URI eventUri, Gate gate) {
        FaultEvent faultEvent = faultEventDao
                .find(eventUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find failure mode"));

        if (faultEvent.getInputGate() != null) {
            throw new LogicViolationException("Event already has a gate");
        }

        faultEvent.setInputGate(gate);
        gate.setProducedEvent(faultEvent);
        faultEventDao.update(faultEvent);

        return gate.getUri();
    }

}
