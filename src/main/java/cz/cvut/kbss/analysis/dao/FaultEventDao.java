package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.exception.PersistenceException;
import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.model.diagram.Rectangle;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import cz.cvut.kbss.jopa.model.metamodel.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FaultEventDao extends NamedEntityDao<FaultEvent> {

    public static final URI PROBABILITY_PROP = URI.create(Vocabulary.s_p_probability);

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

    public URI getSystem(URI faultEventIri){
       return em.createNativeQuery("""
                            PREFIX fta: <http://onto.fel.cvut.cz/ontologies/fta-fmea-application/>
                            
                            SELECT ?systemUri {
                                ?uri a ?type.
                                ?rootEvent fta:has-child ?uri .
                                ?fta fta:is-manifested-by ?rootEvent .
                                ?rootEvent fta:is-derived-from ?rootEventType.
                                ?rootEventType fta:is-manifestation-of ?behavior .
                                ?behavior fta:has-component ?_subsystemUri.
                                ?_subsystemUri fta:is-part-of* ?systemUri.
                                FILTER NOT EXISTS{
                                  ?systemUri fta:is-part-of ?system2.
                                }
                            }""", URI.class)
               .setParameter("type", typeUri)
               .setParameter("uri", faultEventIri)
               .getSingleResult();
    }

    public Optional<FaultEvent> getParent(URI faultEventIri) {

        URI uri = em
                .createNativeQuery("SELECT ?x WHERE { ?x a ?type ; ?hasChildren ?eventUri . }", URI.class)
                .setParameter("type", typeUri)
                .setParameter("hasChildren", URI.create(Vocabulary.s_p_has_child))
                .setParameter("eventUri", faultEventIri)
                .getSingleResult();

        return find(uri);
    }

    public void deleteChildSequence(Collection<URI> faultEventUris){
        String values = """
                VALUES ?e {
                    %s
                }
                """.formatted(
                        faultEventUris.stream()
                                .map(u -> String.format("<%s>", u))
                                .collect(Collectors.joining("\n"))
        );
        em.createNativeQuery("""
                PREFIX : <http://onto.fel.cvut.cz/ontologies/fta-fmea-application/>
                PREFIX seq: <http://krizik.felk.cvut.cz/ontologies/2008/6/sequences.owl#>
                PREFIX seq1: <http://krizik.felk.cvut.cz/ontologies/2008/sequences.owl#>
                
                DELETE{
                    ?e :has-child-sequence ?s .
                    ?sub seq1:hasNext ?next .
                    ?sub seq:hasContents ?cont .
                }WHERE{
                    ?e :has-child-sequence ?s .
                    {
                        BIND(?s as ?sub)
                    }UNION{
                        ?s seq1:hasNext+ ?sub .
                    }
                    OPTIONAL{
                        ?sub seq1:hasNext ?next .
                    }
                    ?sub seq:hasContents ?cont .
                    %s
                }
                """.formatted(values))
                .executeUpdate();
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
        entityDescriptor.addAttributeContext(fe.getAttribute("behavior"), null);

        return entityDescriptor;
    }

    public void setProbability(URI faultEventUri, Double probability, URI context){
        addOrReplaceValue(faultEventUri, PROBABILITY_PROP, probability, context);
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
