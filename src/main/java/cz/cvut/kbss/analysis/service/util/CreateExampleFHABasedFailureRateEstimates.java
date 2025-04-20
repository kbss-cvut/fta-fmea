package cz.cvut.kbss.analysis.service.util;

import cz.cvut.kbss.analysis.model.FailureRate;
import cz.cvut.kbss.analysis.model.FaultEventType;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;

import java.net.URI;
import java.util.*;
import java.util.function.Function;

public class CreateExampleFHABasedFailureRateEstimates {
    protected EntityManager em;

    public CreateExampleFHABasedFailureRateEstimates() {

    }

    public CreateExampleFHABasedFailureRateEstimates(EntityManager em) {
        this.em = em;
    }


    public List<FaultEventType> generateFHABasedFailureRateEstimates(Function<Double, FailureRate> failureRateSupplier){
        Map<URI, List<URI>> fautlEventURIs = getFaultEventURIs();
        List<FaultEventType> ret = new ArrayList<>();
        Random r = new Random();
        for(Map.Entry<URI, List<URI>> e: fautlEventURIs.entrySet()){
            EntityDescriptor entityDescriptor = new EntityDescriptor(e.getKey());
            for(URI feu : e.getValue()) {
                FaultEventType fet = em.find(FaultEventType.class, feu);

                Double failureRateValue =  (1 + r.nextInt(10)) / 100.;
                FailureRate fr = failureRateSupplier.apply(failureRateValue);
                fet.setFailureRate(fr);

                em.merge(fet, entityDescriptor);
                ret.add(fet);
            }

        }
        return ret;
    }

    public Map<URI, List<URI>> getFaultEventURIs(){
        List<URI> contexts = getNamedGraphs();
        Map<URI, List<URI>> ret = new HashMap<>();
        for(URI context : contexts){
            List<URI> faultEvents = getFaultEventURIs(context);
            if(faultEvents == null || faultEvents.isEmpty())
                continue;

            ret.put(context, faultEvents);
        }

        return ret;
    }
    public List<URI> getFaultEventURIs(URI context){
        return em.createNativeQuery("""
                PREFIX fta: <http://onto.fel.cvut.cz/ontologies/fta-fmea-application/>
                SELECT ?fe ?fhaName WHERE {
                    GRAPH ?context {
                        ?ata a fta:ata-system ;
                             fta:ata-code ?ataCode ;
                             fta:name ?ataName ;
                             fta:has-failure-mode ?fm .
                        ?fm fta:is-manifested-by ?fe .
                        ?fe fta:name ?feName .
                    }
                    ?fhaFe fta:is-derived-from ?fe .
                    ?fhaFe a fta:fha-fault-event ;
                    	fta:name ?fhaName .
                    ?fm1 fta:is-manifested-by ?fhaFe.
                    ?fm1 fta:has-component ?c.
                    
                    ?ac fta:name ?acName .
                    ?c fta:is-part-of+ ?ac.
                    FILTER NOT EXISTS{
                        ?fe fta:has-failure-rate ?fr.
                        ?fr fta:has-estimate ?estimate. 
                    }            
                    FILTER NOT EXISTS{
                        ?ac fta:is-part-of+ ?ac2.
                    }
                }ORDER BY ?acName ?fhaName
                """, URI.class)
                .setParameter("context", context)
                .getResultList();
    }

    public List<URI> getNamedGraphs(){
        return em.createNativeQuery("""
                SELECT DISTINCT ?g {
                    GRAPH ?g {
                        ?s ?p ?o.
                    }
                }
    """, URI.class).getResultList();
    }
}

