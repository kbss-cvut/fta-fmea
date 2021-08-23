package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.Function;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.net.URI;

@Repository
public class FunctionDao extends BaseDao<Function> {

    @Autowired
    protected FunctionDao(EntityManager em, PersistenceConf config) {
        super(Function.class, em, config);
    }

    public Component getComponent(URI functionUri) {
        return em
                .createNativeQuery("SELECT ?component WHERE { ?component ?hasFunction ?function }", Component.class)
                .setParameter("hasFunction", URI.create(Vocabulary.s_p_hasFunction))
                .setParameter("function", functionUri)
                .getResultList().stream().findFirst().orElse(null);
    }
}
