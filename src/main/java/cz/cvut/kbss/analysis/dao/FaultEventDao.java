package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.stereotype.Repository;

import java.net.URI;

@Repository
public class FaultEventDao extends BaseDao<FaultEvent> {
    protected FaultEventDao(EntityManager em, PersistenceConf config) {
        super(FaultEvent.class, em, config);
    }

    public boolean isChild(URI faultEventIri) {
        return em
                .createNativeQuery("ASK WHERE { ?x a ?type ; ?hasChildren ?eventUri . }", Boolean.class)
                .setParameter("type", typeUri)
                .setParameter("hasChildren", URI.create(Vocabulary.s_p_hasChildren))
                .setParameter("eventUri", faultEventIri)
                .getSingleResult();
    }

}
