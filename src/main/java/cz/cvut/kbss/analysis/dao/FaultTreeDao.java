package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.FaultTree;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.net.URI;

@Repository
public class FaultTreeDao extends NamedEntityDao<FaultTree> {

    @Autowired
    protected FaultTreeDao(EntityManager em, PersistenceConf config, IdentifierService identifierService) {
        super(FaultTree.class, em, config, identifierService);
    }

    public boolean isRootEvent(URI faultEventIri) {
        return em
                .createNativeQuery("ASK WHERE { ?x a ?type ; ?isManifestedBy ?eventUri . }", Boolean.class)
                .setParameter("type", typeUri)
                .setParameter("isManifestedBy", URI.create(Vocabulary.s_p_is_manifested_by))
                .setParameter("eventUri", faultEventIri)
                .getSingleResult();
    }

}
