package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.FaultEventScenario;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class FaultEventScenarioDao extends BaseDao<FaultEventScenario> {
    protected FaultEventScenarioDao(EntityManager em, PersistenceConf config) {
        super(FaultEventScenario.class, em, config);
    }
}
