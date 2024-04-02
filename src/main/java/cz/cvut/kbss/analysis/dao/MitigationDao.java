package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.Mitigation;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MitigationDao extends BehaviorDao<Mitigation> {

    @Autowired
    protected MitigationDao(EntityManager em, PersistenceConf config, IdentifierService identifierService) {
        super(Mitigation.class, em, config, identifierService);
    }
}
