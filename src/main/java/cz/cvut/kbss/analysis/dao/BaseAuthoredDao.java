package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.exception.PersistenceException;
import cz.cvut.kbss.analysis.model.HasAuthorData;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;

import java.net.URI;
import java.util.List;

public abstract class BaseAuthoredDao<T extends HasAuthorData> extends BaseDao<T> implements AuthoredGenericDao<T> {

    protected BaseAuthoredDao(Class<T> type, EntityManager em, PersistenceConf config) {
        super(type, em, config);
    }

    @Override
    public List<T> findAllForUser(User user) {
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
