package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.FaultEventScenario;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.net.URI;

@Repository
public class FaultEventScenarioDao extends BaseDao<FaultEventScenario> {
    @Autowired
    protected FaultEventScenarioDao(EntityManager em, PersistenceConf config, IdentifierService identifierService) {
        super(FaultEventScenario.class, em, config, identifierService);
    }

    @Override
    public EntityDescriptor getEntityDescriptor(URI uri) {
        return super.getEntityDescriptor(uri);
    }
}
