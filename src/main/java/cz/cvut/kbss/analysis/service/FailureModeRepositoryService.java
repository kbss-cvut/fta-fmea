package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FailureModeDao;
import cz.cvut.kbss.analysis.model.FailureMode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FailureModeRepositoryService {

    private final FailureModeDao failureModeDao;

    @Transactional
    public List<FailureMode> findAll() {
        return failureModeDao.findAll();
    }

    @Transactional
    public URI persist(FailureMode failureMode) {
        failureModeDao.persist(failureMode);
        return failureMode.getUri();
    }

    @Transactional
    public void delete(FailureMode failureMode) {
        failureModeDao.remove(failureMode);
    }

}