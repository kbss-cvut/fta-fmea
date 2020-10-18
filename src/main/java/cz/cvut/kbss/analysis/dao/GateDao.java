package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.Gate;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class GateDao extends BaseDao<Gate> {
    protected GateDao(EntityManager em, PersistenceConf config) {
        super(Gate.class, em, config);
    }
}
