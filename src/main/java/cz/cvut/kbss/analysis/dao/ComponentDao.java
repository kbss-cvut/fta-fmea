package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.exception.PersistenceException;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.util.List;

@Repository
public class ComponentDao extends BaseDao<Component> {

    @Autowired
    public ComponentDao(EntityManager em, PersistenceConf config) {
        super(Component.class, em, config);
    }

    public List<Component> findAllForUser(User user) {
        try {
            return em.createNativeQuery("SELECT ?x WHERE { ?x a ?type ; ?authoredBy ?userUri . }", type)
                    .setParameter("type", typeUri)
                    .setParameter("authoredBy", URI.create(Vocabulary.s_p_authoredBy))
                    .setParameter("userUri", user.getUri())
                    .getResultList();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

}
