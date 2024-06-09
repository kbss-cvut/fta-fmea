package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.exception.PersistenceException;
import cz.cvut.kbss.analysis.model.System;
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
import java.util.*;

@Repository
public class FaultEventDao extends NamedEntityDao<FaultEvent> {

    public final static URI DERIVED_FROM_PROP = URI.create(Vocabulary.s_p_is_derived_from);
    public final static URI FTA_EVENT_TYPE_PROP = URI.create(Vocabulary.s_p_fault_event_type);
    public final static URI IS_MANIFESTED_BY_PROP = URI.create(Vocabulary.s_p_is_manifested_by);

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
        entityDescriptor.addAttributeContext(fe.getAttribute("behavior"), null);

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

    public Optional<Event> findEvent(URI id){
        Objects.requireNonNull(id);
        try {
            EntityDescriptor entityDescriptor = getEntityDescriptor(id);
            return Optional.ofNullable(em.find(Event.class, id, entityDescriptor));
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public void loadManagedSupertypesOrCreate(FaultEvent faultEvent, NamedEntity system, URI context){
        if(faultEvent.getSupertypes() == null || faultEvent.getSupertypes().isEmpty())
            return;
        Set<Event> newSupertypes = new HashSet<>();
        Set<Event> managedSupertypes = new HashSet<>();
        Set<Event> unmanagedSupertypes = faultEvent.getSupertypes();
        faultEvent.setSupertypes(managedSupertypes);

        for(Event event : unmanagedSupertypes){
            Optional<Event> opt = event.getUri() != null ?
                    findEvent(event.getUri()) :
                    Optional.ofNullable(null);
            if(opt.isPresent())
                managedSupertypes.add(opt.get());
            else
                newSupertypes.add(event);
        }

        if(newSupertypes.isEmpty())
            return;

        System managedSystem = em.find(System.class, system.getUri());

        EntityDescriptor entityDescriptor = new EntityDescriptor(context);
        for(Event evt : newSupertypes){
            FailureMode fm = new FailureMode();
            fm.setName(evt.getName() + " failure mode");
            fm.setItem(managedSystem);
            evt.setBehavior(fm);

            em.persist(evt, entityDescriptor);
            managedSupertypes.add(evt);
        }
    }

    /**
     * Replaces the supertypes of the faultEvent argument, if any, with their managed versions
     * @param faultEvent
     */
    public void loadManagedSupertypes(FaultEvent faultEvent){
        if(faultEvent.getSupertypes() != null) {
            Set<Event> managedSupertypes = new HashSet<>();
            for(Event event : faultEvent.getSupertypes()){
                findEvent(event.getUri()).ifPresent(managedSupertypes::add);
            }
            faultEvent.setSupertypes(managedSupertypes);
        }
    }

    public List<FaultEventReference> getFaultEventRootWithSupertype(URI supertype){
        try{
            return em.createNativeQuery(
                            """
                                    SELECT DISTINCT ?faultEvent ?faultTree WHERE{
                                        ?faultEvent ?derivedFrom ?supertype.
                                        ?faultEvent ?ftaEventTypeProp ?ftaEventType.
                                        ?faultEvent a ?type.
                                        ?faultTree ?isManifestedByProp ?faultEvent
                                    }""", "FaultEventReference")
                    .setParameter("derivedFrom", DERIVED_FROM_PROP)
                    .setParameter("supertype", supertype)
                    .setParameter("ftaEventTypeProp", FTA_EVENT_TYPE_PROP)
                    .setParameter("type", this.typeUri)
                    .setParameter("isManifestedByProp", IS_MANIFESTED_BY_PROP)
                    .getResultList();
        }catch (RuntimeException e){
            throw new PersistenceException(e);
        }
    }

    public List<FaultEventType> getTopFaultEvents(URI systemUri) {
        try{
            List<FaultEventTypeSummary> ret = em.createNativeQuery("""
                        PREFIX fta: <http://onto.fel.cvut.cz/ontologies/fta-fmea-application/>
                        SELECT ?uri ?name ?componentName ?eventType {
                            ?uri a fta:fha-fault-event ;
                                fta:name ?name ;
                                fta:is-manifestation-of/fta:has-component/((^fta:has-part-component)+) ?system ;
                                fta:is-derived-from ?generalEvent .
                            
                            FILTER NOT EXISTS{
                                ?system1 fta:has-part-component ?system.
                            }
                         
                            ?generalEvent fta:is-manifestation-of ?fm .
                            ?fm fta:has-component ?component .
                            
                            ?component fta:name ?componentLabel ;
                                         fta:ata-code ?code .
                            BIND(CONCAT(str(?code), " - ", str(?componentLabel)) as ?componentName)
                            BIND("INTERMEDIATE" as ?eventType)       
                        }
                    """, "FaultEventSummary")
                    .setParameter("system", systemUri)
                    .getResultList();
            List<FaultEventType> ret1 = ret.stream().map(fe -> fe.asEntity(FaultEventType.class)).toList();
            return ret1;
        }catch (RuntimeException e){
            throw new PersistenceException(e);
        }
    }

    public List<FaultEventType> getAllFaultEvents(URI systemUri) {
        try{
            List<FaultEventTypeSummary> ret = em.createNativeQuery("""
                        PREFIX fta: <http://onto.fel.cvut.cz/ontologies/fta-fmea-application/>
                        SELECT ?uri ?name ?componentName ?eventType {
                            ?uri a ?eventClass.
                            FILTER(?eventClass in (fta:fha-fault-event, fta:fault-event-type))
                            ?uri fta:name ?name ;
                                 fta:is-manifestation-of/fta:has-component/((^fta:has-part-component)+) ?system ;
                                 fta:is-derived-from ?generalEvent .
                                
                            
                            FILTER NOT EXISTS{
                                ?system1 fta:has-part-component ?system.
                            }
                         
                            ?generalEvent fta:is-manifestation-of ?fm .
                            ?fm fta:has-component ?component .
                            
                            ?component fta:name ?componentLabel ;
                                         fta:ata-code ?code .
                            BIND(CONCAT(str(?code), " - ", str(?componentLabel)) as ?componentName)
                            
                            BIND(IF(?eventClass = fta:fha-fault-event, "INTERMEDIATE", "BASIC") as ?eventType)       
                        }
                    """, "FaultEventSummary")
                    .setParameter("system", systemUri)
                    .getResultList();

            List<FaultEventType> ret1 = ret.stream().map(fe -> fe.asEntity(FaultEventType.class)).toList();
            return ret1;
        }catch (RuntimeException e){
            throw new PersistenceException(e);
        }
    }
}
