package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.FailureModesRow;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class FailureModesRowDao extends BaseDao<FailureModesRow> {
    protected FailureModesRowDao(EntityManager em, PersistenceConf config) {
        super(FailureModesRow.class, em, config);
    }
}
