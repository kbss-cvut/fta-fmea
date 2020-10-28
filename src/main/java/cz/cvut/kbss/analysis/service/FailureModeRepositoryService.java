package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FailureModeDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FailureModeRepositoryService {

    private final FailureModeDao failureModeDao;

    public List<FailureMode> findAllForUser(User user) {
        return failureModeDao.findAllForUser(user);
    }

    public FailureMode find(URI failureModeUri) {
        return getNode(failureModeUri);
    }

    @Transactional
    public FailureMode create(FailureMode failureMode) {
        failureModeDao.persist(failureMode);
        return failureMode;
    }

    public Set<Mitigation> getMitigation(URI failureModeUri) {
        FailureMode failureMode = getNode(failureModeUri);

        return failureMode.getMitigation();
    }

    @Transactional
    public Mitigation addMitigation(URI failureModeUri, Mitigation mitigation) {
        FailureMode failureMode = getNode(failureModeUri);

        failureMode.addMitigation(mitigation);
        failureModeDao.update(failureMode);

        return mitigation;
    }

    private FailureMode getNode(URI failureModeUri) {
        return failureModeDao
                .find(failureModeUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find failure mode"));
    }
}