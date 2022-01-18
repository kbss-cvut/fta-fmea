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
    public FailureMode createFailureMode(FailureMode failureMode){
        persist(failureMode);
        return failureMode;
    }

    @Transactional
    public FailureMode updateFailureModeProperties(FailureMode failureModeProperties){
        log.info("> updateFailureModeProperties - {}", failureModeProperties.getUri());

        FailureMode failureMode = getPrimaryDao().find(failureModeProperties.getUri()).orElse(null);

        failureMode.setName(failureModeProperties.getName());
        failureMode.setBehaviorType(failureModeProperties.getBehaviorType());

        return failureMode;
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

    @Transactional
    public FailureMode addRequiredBehavior(URI failureModeUri, URI requiredBehaviorUri) {
        log.info("> addRequiredBehavior - {}, {}", failureModeUri, requiredBehaviorUri);

        FailureMode failureMode = findRequired(failureModeUri);
        failureMode.getRequiredBehaviors().add(findRequired(requiredBehaviorUri));

        return update(failureMode);
    }
    @Transactional
    public void removeRequiredBehavior(URI failureModeUri, URI requiredBehaviorUri) {
        log.info("> removeRequiredBehavior - {}, {}", failureModeUri, requiredBehaviorUri);

        FailureMode failureMode = findRequired(failureModeUri);
        failureMode.getRequiredBehaviors().removeIf(behavior -> behavior.getUri().equals(requiredBehaviorUri));

        update(failureMode);
        log.info("> removeRequiredBehavior - removed");
    }

    @Transactional
    public FailureMode addChildBehavior(URI failureModeUri, URI childBehaviorUri) {
        log.info("> addChildBehavior - {}, {}", failureModeUri, childBehaviorUri);

        FailureMode failureMode = findRequired(failureModeUri);
        failureMode.getChildBehaviors().add(findRequired(childBehaviorUri));

        return update(failureMode);
    }
    @Transactional
    public void removeChildBehavior(URI failureModeUri, URI childBehaviorUri) {
        log.info("> removeChildBehavior - {}, {}", failureModeUri, childBehaviorUri);

        FailureMode failureMode = findRequired(failureModeUri);
        failureMode.getChildBehaviors().removeIf(behavior -> behavior.getUri().equals(childBehaviorUri));

        update(failureMode);
        log.info("> removeChildBehavior - removed");
    }


}