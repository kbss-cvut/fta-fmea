package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class FaultEventDao extends BaseDao<FaultEvent> {
    protected FaultEventDao(EntityManager em, PersistenceConf config) {
        super(FaultEvent.class, em, config);
    }
}
