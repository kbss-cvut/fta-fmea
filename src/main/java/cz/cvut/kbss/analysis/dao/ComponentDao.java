package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.exception.PersistenceException;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.net.URI;

@Repository
public class ComponentDao extends BaseDao<Component> {

    @Autowired
    public ComponentDao(EntityManager em, PersistenceConf config) {
        super(Component.class, em, config);
    }

    public Component findByFailureMode(FailureMode failureMode) {
        try {
            // TODO delete? em.refresh(failureMode);

            return em.createNativeQuery("SELECT ?x WHERE { ?x a ?type ; ?hasFm ?fmUri . }", type)
                    .setParameter("type", typeUri)
                    .setParameter("hasFm", URI.create(Vocabulary.s_p_hasFailureMode))
                    .setParameter("fmUri", failureMode.getUri())
                    .getSingleResult();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

}
