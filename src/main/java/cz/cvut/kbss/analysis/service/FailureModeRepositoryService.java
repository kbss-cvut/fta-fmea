package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FailureModeDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.model.FailureMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;

@Service
@Slf4j
public class FailureModeRepositoryService extends BaseRepositoryService<FailureMode> {

    private final FailureModeDao failureModeDao;

    @Autowired
    public FailureModeRepositoryService(@Qualifier("defaultValidator") Validator validator, FailureModeDao failureModeDao) {
        super(validator);
        this.failureModeDao = failureModeDao;
    }

    @Override
    protected GenericDao<FailureMode> getPrimaryDao() {
        return failureModeDao;
    }


}