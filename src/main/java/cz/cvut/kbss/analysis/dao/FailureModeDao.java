package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FailureModeDao extends BehaviorDao<FailureMode> {

    @Autowired
    public FailureModeDao(EntityManager em, PersistenceConf config) {
        super(FailureMode.class, em, config);
    }

}
