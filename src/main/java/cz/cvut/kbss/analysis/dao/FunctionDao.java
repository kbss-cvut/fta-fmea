package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.model.Function;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.util.List;

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

    public List<FailureMode> getImpairedBehaviors(URI functionUri){
        return em
                .createNativeQuery("SELECT ?failureMode WHERE { ?failureMode ?impairs ?function }", FailureMode.class)
                .setParameter("impairs", URI.create(Vocabulary.s_p_impairs))
                .setParameter("function", functionUri)
                .getResultList();
    }
}
