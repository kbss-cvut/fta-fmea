package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.ComponentDao;
import cz.cvut.kbss.analysis.dao.FailureModeDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FailureModeRepositoryService {

    private final FailureModeDao failureModeDao;
    private final ComponentDao componentDao;

    @Transactional(readOnly = true)
    public List<FailureMode> findAllForUser(User user) {
        return failureModeDao.findAllForUser(user);
    }

    @Transactional(readOnly = true)
    public FailureMode find(URI failureModeUri) {
        return getFailureMode(failureModeUri);
    }

    @Transactional
    public FailureMode create(FailureMode failureMode) {
        log.info("> create - {}", failureMode);

        failureModeDao.persist(failureMode);

        log.info("< create - {}", failureMode);
        return failureMode;
    }

    @Transactional
    public FailureMode update(FailureMode failureMode) {
        log.info("> update - {}", failureMode);

        failureModeDao.update(failureMode);

        log.info("> update - {}", failureMode);
        return failureMode;
    }

    @Transactional(readOnly = true)
    public Set<Mitigation> getMitigation(URI failureModeUri) {
        FailureMode failureMode = getFailureMode(failureModeUri);

        return failureMode.getMitigation();
    }

    @Transactional
    public Mitigation addMitigation(URI failureModeUri, Mitigation mitigation) {
        log.info("> addMitigation - {}, {}", failureModeUri, mitigation);

        FailureMode failureMode = getFailureMode(failureModeUri);

        failureMode.addMitigation(mitigation);
        failureModeDao.update(failureMode);

        log.info("< addMitigation - {}", mitigation);
        return mitigation;
    }

    @Transactional(readOnly = true)
    public Component getComponent(URI failureModeUri) {
        log.info("> getComponent - {}", failureModeUri);

        FailureMode failureMode = getFailureMode(failureModeUri);
        return componentDao.findByFailureMode(failureMode);
    }

    @Transactional
    public void delete(URI failureModeUri) {
        failureModeDao.remove(failureModeUri);
    }

    private FailureMode getFailureMode(URI failureModeUri) {
        return failureModeDao
                .find(failureModeUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find failure mode"));
    }
}