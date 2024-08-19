package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.System;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.service.security.SecurityUtils;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.util.List;

@Repository
public class SystemDao extends ManagedEntityDao<System> {

    protected static final URI HAS_COMPONENT_PART = URI.create(Vocabulary.s_p_has_part_component);

    @Autowired
    protected SystemDao(EntityManager em, PersistenceConf config, IdentifierService identifierService, SecurityUtils securityUtils) {
        super(System.class, em, config, identifierService, securityUtils);
    }

    @Override
    public EntityDescriptor getEntityDescriptor(System entity) {
        if(entity.getUri() == null)
            entity.setUri(identifierService.generateNewInstanceUri(typeUri.toString()));
        EntityDescriptor entityDescriptor = getEntityDescriptor(entity.getUri());
        return entityDescriptor;
    }

    public EntityDescriptor getEntityDescriptor(URI uri){
        EntityDescriptor descriptor = new EntityDescriptor(uri);
        setEntityDescriptor(descriptor);
        return descriptor;
    }

    public List<Component> findComponents(URI systemURI){
        return em.createNativeQuery("""
                SELECT ?uri {
                    ?systemURI (?hasComponentPartProp)+ ?uri.
                }                    
                """)
                .setParameter("systemURI", systemURI)
                .setParameter("hasComponentPartProp", HAS_COMPONENT_PART)
                .getResultList();
    }
}
