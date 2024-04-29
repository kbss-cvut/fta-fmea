package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.exception.PersistenceException;
import cz.cvut.kbss.analysis.model.NamedEntity;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.List;

public class NamedEntityDao<T extends NamedEntity> extends BaseDao<T> {

    public static URI P_HAS_NAME = URI.create(Vocabulary.s_p_name);
    public static URI P_HAS_DESCRIPTION = URI.create(Vocabulary.s_p_description);

    @Autowired
    protected NamedEntityDao(Class<T> type, EntityManager em, PersistenceConf config, IdentifierService identifierService) {
        super(type, em, config, identifierService);
    }

    public List<T> findAllSummaries(){
        try {
            List<NamedEntity> ret = em.createNativeQuery("SELECT ?uri ?name ?description WHERE { " +
                            "?uri a ?type. \n" +
                            "?uri ?pName ?name. \n" +
                            "OPTIONAL{?uri ?pDescription ?description.} \n" +
                            "}", "Summary")
                    .setParameter("type", typeUri)
                    .setParameter("pName", P_HAS_NAME)
                    .setParameter("pDescription", P_HAS_DESCRIPTION)
                    .getResultList();

            return ret.stream().map(s -> s.asEntity(type)).toList();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

}
