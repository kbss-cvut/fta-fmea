package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.FailureModesTable;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class FailureModesTableDao extends NamedEntityDao<FailureModesTable> {
    protected FailureModesTableDao(EntityManager em, PersistenceConf config, IdentifierService identifierService) {
        super(FailureModesTable.class, em, config, identifierService);
    }
}
