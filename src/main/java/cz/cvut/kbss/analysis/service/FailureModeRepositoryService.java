package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FailureModeDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.Gate;
import cz.cvut.kbss.analysis.model.Mitigation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FailureModeRepositoryService {

    private final FailureModeDao failureModeDao;

    @Transactional(readOnly = true)
    public FailureMode find(URI failureModeUri) {
        return failureModeDao
                .find(failureModeUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find failure mode"));
    }

    @Transactional(readOnly = true)
    public Set<Mitigation> getMitigation(URI failureModeUri) {
        FailureMode failureMode = failureModeDao
                .find(failureModeUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find failure mode"));

        return failureMode.getMitigation();
    }

    @Transactional
    public URI addMitigation(URI failureModeUri, Mitigation mitigation) {
        FailureMode failureMode = failureModeDao
                .find(failureModeUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find failure mode"));

        failureMode.addMitigation(mitigation);
        failureModeDao.update(failureMode);
        return mitigation.getUri();
    }

    @Transactional
    public URI setCausingEvent(URI failureModeUri, FaultEvent causingEvent) {
        FailureMode failureMode = failureModeDao
                .find(failureModeUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find failure mode"));

        // insert a gate in between
        Gate intermediateGate = new Gate();
        intermediateGate.setFailureMode(failureMode);
        failureMode.setManifestingGate(intermediateGate);

        intermediateGate.addInputEvent(causingEvent);

        failureModeDao.update(failureMode);
        return causingEvent.getUri();
    }

}