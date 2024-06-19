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

@Repository
public class FaultEventTypeDao extends  NamedEntityDao<FaultEventType> {

    public final static URI DERIVED_FROM_PROP = URI.create(Vocabulary.s_p_is_derived_from);
    public final static URI FTA_EVENT_TYPE_PROP = URI.create(Vocabulary.s_p_fault_event_type);
    public final static URI IS_MANIFESTED_BY_PROP = URI.create(Vocabulary.s_p_is_manifested_by);

    public FaultEventTypeDao(EntityManager em, PersistenceConf config, IdentifierService identifierService) {
        super(FaultEventType.class, em, config, identifierService);
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
