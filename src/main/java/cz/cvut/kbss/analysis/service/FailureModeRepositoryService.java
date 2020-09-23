package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FailureModeDao;
import cz.cvut.kbss.analysis.model.FailureMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class FailureModeRepositoryService {

    private final FailureModeDao failureModeDao;

    @Autowired
    public FailureModeRepositoryService(FailureModeDao failureModeDao) {
        this.failureModeDao = failureModeDao;
    }

    @Transactional
    public List<FailureMode> findAll() {
        return failureModeDao.findAll();
    }

    @Transactional
    public void persist(FailureMode failureMode) {
        Objects.requireNonNull(failureMode);
        failureModeDao.persist(failureMode);
    }

    @Transactional
    public void delete(FailureMode failureMode) {
        Objects.requireNonNull(failureMode);
        failureModeDao.delete(failureMode);
    }

}