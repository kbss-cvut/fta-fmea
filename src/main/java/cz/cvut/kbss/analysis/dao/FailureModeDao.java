package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.net.URI;

@Repository
public class FailureModeDao extends BehaviorDao<FailureMode> {

    protected static final URI IS_MANIFESTED_BY_PROP = URI.create(Vocabulary.s_p_is_manifested_by);

    @Autowired
    public FailureModeDao(EntityManager em, PersistenceConf config, IdentifierService identifierService) {
        super(FailureMode.class, em, config, identifierService);
    }

    public FailureMode findByEvent(URI eventUir) {
        return em.createNativeQuery("""
                SELECT ?uri {
                    ?uri ?manifestedByProp ?eventUri.
                }
                """, FailureMode.class)
                .setParameter("manifestedByProp", IS_MANIFESTED_BY_PROP)
                .setParameter("eventUri", eventUir)
                .getSingleResult();
    }
}
