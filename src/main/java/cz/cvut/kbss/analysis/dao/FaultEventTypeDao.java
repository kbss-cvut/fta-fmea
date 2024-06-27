package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.exception.PersistenceException;
import cz.cvut.kbss.analysis.model.FaultEventReference;
import cz.cvut.kbss.analysis.model.FaultEventType;
import cz.cvut.kbss.analysis.model.FaultEventTypeSummary;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Repository
public class FaultEventTypeDao extends  NamedEntityDao<FaultEventType> {

    public final static URI DERIVED_FROM_PROP = URI.create(Vocabulary.s_p_is_derived_from);
    public final static URI FTA_EVENT_TYPE_PROP = URI.create(Vocabulary.s_p_fault_event_type);
    public final static URI IS_MANIFESTED_BY_PROP = URI.create(Vocabulary.s_p_is_manifested_by);
    public final static URI PROBABILITY_PROP = URI.create(Vocabulary.s_p_probability);
    public final static URI STATUS_PROP = URI.create(Vocabulary.s_p_status);
    public final static URI FAULT_EVENT_TYPE = URI.create(Vocabulary.s_c_fault_event);

    public FaultEventTypeDao(EntityManager em, PersistenceConf config, IdentifierService identifierService) {
        super(FaultEventType.class, em, config, identifierService);
    }

    public List<FaultEventReference> getFaultEventRootWithSupertype(URI supertype){
        try{
            return em.createNativeQuery(
                            """
                                    SELECT DISTINCT ?faultEvent ?faultTree ?probability ?status WHERE{
                                        ?faultEvent ?derivedFrom ?supertype.
                                        ?faultEvent ?ftaEventTypeProp ?ftaEventType.
                                        OPTIONAL{ ?faultEvent ?probabilityProp ?probability. }
                                        ?faultEvent a ?type.
                                        ?faultTree ?isManifestedByProp ?faultEvent.
                                        OPTIONAL{ ?faultTree ?statusProp ?status. }
                                    }""", "FaultEventReference")
                    .setParameter("derivedFrom", DERIVED_FROM_PROP)
                    .setParameter("supertype", supertype)
                    .setParameter("ftaEventTypeProp", FTA_EVENT_TYPE_PROP)
                    .setParameter("probabilityProp", PROBABILITY_PROP)
                    .setParameter("statusProp", STATUS_PROP)
                    .setParameter("type", FAULT_EVENT_TYPE)
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
                                fta:is-manifestation-of/fta:has-component ?c.
                            
                            ?system fta:has-part-component+ ?c.
                            ?c fta:is-derived-from ?generalComponent .
                            
                            FILTER NOT EXISTS{
                                ?system1 fta:has-part-component ?system.
                            }
                    
                            ?generalComponent fta:name ?componentLabel ;
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
                                fta:is-manifestation-of/fta:has-component ?c.
                            
                            ?system fta:has-part-component+ ?c.
                            ?c fta:is-derived-from ?generalComponent .
                                                                                               
                            FILTER NOT EXISTS{
                               ?system1 fta:has-part-component ?system.
                            }
                            
                            ?generalComponent fta:name ?componentLabel ;
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

    public FaultEventType getFaultEventSupertype(URI faultEventUri){
        Optional<URI> optUri = em.createNativeQuery("""
                SELECT ?uri {
                    ?faultEventUri ?isDerivedFromProp ?uri.
                }
                """,URI.class)
                .setParameter("isDerivedFromProp", DERIVED_FROM_PROP)
                .setParameter("faultEventUri", faultEventUri)
                .getResultStream().limit(1).findAny();
        return optUri.map(u -> find(u).orElse(null)).orElse(null);
    }
}
