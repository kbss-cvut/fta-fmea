package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.jopa.model.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FailureModeDao extends BaseDao<FailureMode> {

    @Autowired
    public FailureModeDao(EntityManager em, PersistenceConf config) {
        super(FailureMode.class, em, config);
    }

}
