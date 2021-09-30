package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FailureModeDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.model.FailureMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.net.URI;

@Service
@Slf4j
public class FailureModeRepositoryService extends BaseRepositoryService<FailureMode> {

    private final FailureModeDao failureModeDao;
    private final FunctionRepositoryService functionRepositoryService;

    @Autowired
    public FailureModeRepositoryService(@Qualifier("defaultValidator") Validator validator, FailureModeDao failureModeDao, FunctionRepositoryService functionRepositoryService) {
        super(validator);
        this.failureModeDao = failureModeDao;
        this.functionRepositoryService = functionRepositoryService;
    }

    @Override
    protected GenericDao<FailureMode> getPrimaryDao() {
        return failureModeDao;
    }

    @Transactional
    public FailureMode addImpairedBehavior(URI failureModeUri, URI impairedBehaviorUri) {
        log.info("> addImpairedBehavior - {}, {}", failureModeUri, impairedBehaviorUri);

        FailureMode failureMode = findRequired(failureModeUri);
        failureMode.getImpairedBehaviors().add(functionRepositoryService.findRequired(impairedBehaviorUri));

        return update(failureMode);
    }

    @Transactional
    public void removeImpairedBehavior(URI failureModeUri, URI impairedBehaviorUri) {
        log.info("> removeImpairedBehavior - {}, {}", failureModeUri, impairedBehaviorUri);

        FailureMode failureMode = findRequired(failureModeUri);
        failureMode.getImpairedBehaviors().removeIf(behavior -> behavior.getUri().equals(impairedBehaviorUri));

        update(failureMode);
        log.info("> removeImpairedBehavior - removed");
    }

}