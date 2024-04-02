package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ComponentDao extends NamedEntityDao<Component> {

    @Autowired
    public ComponentDao(EntityManager em, PersistenceConf config, IdentifierService identifierService) {
        super(Component.class, em, config, identifierService);
    }

}
