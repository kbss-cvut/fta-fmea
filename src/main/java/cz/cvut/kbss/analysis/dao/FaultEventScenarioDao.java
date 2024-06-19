package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.FaultEventScenario;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.net.URI;

@Repository
public class FaultEventScenarioDao extends BaseDao<FaultEventScenario> {

    public static URI HAS_SCENARIO_PROP = URI.create(Vocabulary.s_p_has_scenario);
    @Autowired
    protected FaultEventScenarioDao(EntityManager em, PersistenceConf config, IdentifierService identifierService) {
        super(FaultEventScenario.class, em, config, identifierService);
    }

    @Override
    public EntityDescriptor getEntityDescriptor(URI uri) {
        return super.getEntityDescriptor(uri);
    }

    public void addScenarioToTree(URI faultTreeUri, FaultEventScenario scenario){
        persistPropertyInContext(faultTreeUri, HAS_SCENARIO_PROP, scenario.getUri(), scenario.getContext());
    }

    public void removeScenarios(URI faultTreeUri){
        removeAll(faultTreeUri, HAS_SCENARIO_PROP, faultTreeUri);
    }
}
