package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.exception.PersistenceException;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.diagram.Rectangle;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import cz.cvut.kbss.jopa.model.metamodel.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.net.URI;

@Repository
public class FaultEventDao extends NamedEntityDao<FaultEvent> {
    @Autowired
    protected FaultEventDao(EntityManager em, PersistenceConf config, IdentifierService identifierService) {
        super(FaultEvent.class, em, config, identifierService);
    }

    public boolean isChild(URI faultEventIri) {
        return em
                .createNativeQuery("ASK WHERE { ?x a ?type ; ?hasChildren ?eventUri . }", Boolean.class)
                .setParameter("type", typeUri)
                .setParameter("hasChildren", URI.create(Vocabulary.s_p_has_child))
                .setParameter("eventUri", faultEventIri)
                .getSingleResult();
    }

    @Override
    public EntityDescriptor getEntityDescriptor(FaultEvent entity) {
        URI graph = getContext(entity);
        return getEntityDescriptorInContext(graph);
    }

    @Override
    public EntityDescriptor getEntityDescriptor(URI uri) {
        URI graph = getContext(uri);
        return getEntityDescriptorInContext(graph);
    }

    protected EntityDescriptor getEntityDescriptorInContext(URI graph){
        EntityDescriptor entityDescriptor = new EntityDescriptor(graph);
        EntityType<FaultEvent> fe = em.getMetamodel().entity(FaultEvent.class);
        entityDescriptor.addAttributeContext(fe.getAttribute("supertypes"), null);
        // TODO - consider not persisting failure mode in graph context
        return entityDescriptor;
    }

    public EntityDescriptor getRectangleDescriptor(URI uri){
        URI graph = getContext(uri);
        EntityDescriptor entityDescriptor = new EntityDescriptor(graph);
        return entityDescriptor;
    }

    public Rectangle update(Rectangle rect){
        try{
            EntityDescriptor entityDescriptor = getRectangleDescriptor(rect.getUri());
            return em.merge(rect, entityDescriptor);
        }catch (RuntimeException e){
            throw new PersistenceException(e);
        }
    }
}
