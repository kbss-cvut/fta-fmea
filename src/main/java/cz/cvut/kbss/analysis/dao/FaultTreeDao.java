package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.exception.PersistenceException;
import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.service.security.SecurityUtils;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import cz.cvut.kbss.jopa.model.metamodel.EntityType;
import cz.cvut.kbss.jopa.model.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.util.*;
import java.util.stream.Stream;

@Repository
public class FaultTreeDao extends ManagedEntityDao<FaultTree> {

    public static final URI STATUS_PROP = URI.create(Vocabulary.s_p_status);

    @Autowired
    protected FaultTreeDao(EntityManager em, PersistenceConf config, IdentifierService identifierService, SecurityUtils securityUtils) {
        super(FaultTree.class, em, config, identifierService, securityUtils);
    }

    public boolean isRootEvent(URI faultEventIri) {
        return em
                .createNativeQuery("ASK WHERE { ?x a ?type ; ?isManifestedBy ?eventUri . }", Boolean.class)
                .setParameter("type", typeUri)
                .setParameter("isManifestedBy", URI.create(Vocabulary.s_p_is_manifested_by))
                .setParameter("eventUri", faultEventIri)
                .getSingleResult();
    }

    @Override
    public EntityDescriptor getEntityDescriptor(FaultTree entity) {
        if(entity.getUri() == null)
            entity.setUri(identifierService.generateNewInstanceUri(typeUri.toString()));
        EntityDescriptor entityDescriptor = getEntityDescriptor(entity.getUri());
        return entityDescriptor;
    }

    @Override
    public EntityDescriptor getEntityDescriptor(URI uri) {
        EntityDescriptor entityDescriptor = new EntityDescriptor(uri);
        super.setEntityDescriptor(entityDescriptor);
        EntityType<FaultTree> ft = em.getMetamodel().entity(type);
        entityDescriptor.addAttributeContext(ft.getAttribute("failureModesTable"), null);

        return entityDescriptor;
    }

    @Override
    public Optional<FaultTree> find(URI id) {
        Optional<FaultTree> faultTreeOpt = super.find(id);
        if(faultTreeOpt.isEmpty())
            return faultTreeOpt;
        FaultTree faultTree = faultTreeOpt.get();
        return Optional.of(faultTree);
    }

    public Collection<Event> getRelatedEventTypes(FaultTree faultTree){
        Set<Event> eventTypes = new HashSet<>();

        for(FaultEvent faultEvent : faultTree.getAllEvents()){
            if(faultEvent.getSupertypes() == null)
                continue;
            faultEvent.getSupertypes().stream()
                    .flatMap(t -> Stream.concat(
                            Stream.of(t),
                            t.getSupertypes() != null ? t.getSupertypes().stream() : Stream.of()))
                    .forEach(eventTypes::add);
        }
        return eventTypes;
    }

    @Override
    public List<FaultTree> findAllSummaries() {
        try {
            List<FaultTreeSummary> ret = getSummariesQuery()
                    .getResultList();
            return ret.stream().map(s -> s.asEntity(type)).toList();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public FaultTree findSummary(URI managedEntityUri) {
        try {
            Query query = getSummariesQuery();
            query.setParameter("_uri", managedEntityUri);
            FaultTreeSummary ret = (FaultTreeSummary)query.getSingleResult();
            return ret.asEntity(type);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public Query getSummariesQuery() {
             return em.createNativeQuery("""
                            PREFIX fta: <http://onto.fel.cvut.cz/ontologies/fta-fmea-application/>
                            SELECT * WHERE {
                                BIND(?_uri as ?uri) 
                                ?uri a ?type. 
                                ?uri ?pName ?name.
                                OPTIONAL{?uri ?pDescription ?description.} 
                                OPTIONAL{?uri ?pCreated ?created.}
                                OPTIONAL{?uri ?pModified ?modified.}
                                OPTIONAL{?uri ?pCreator ?creator.}
                                OPTIONAL{?uri ?pLastEditor ?lastEditor.}
                                OPTIONAL{ 
                                    ?uri fta:is-manifested-by ?rootEvent .
                                    ?rootEvent fta:is-derived-from ?rootEventType.
                                    OPTIONAL{
                                        ?rootEvent fta:probability ?calculatedFailureRate.
                                    }
                                    OPTIONAL{
                                        ?rootEventType fta:auxiliary ?auxiliary.
                                    }
                                    OPTIONAL{
                                        ?rootEventType fta:has-failure-rate ?failureRate.
                                        ?failureRate fta:has-requirement ?failureRateRequirement.
                                        ?failureRateRequirement fta:to ?requiredFailureRate.
                                    }
                                    OPTIONAL{
                                        ?rootEventType fta:is-derived-from ?supsup.
                                        ?supsup fta:has-failure-rate ?fhaFailureRateQ.
                                        ?fhaFailureRateQ fta:has-estimate ?fhaFailureRateP.
                                        ?fhaFailureRateP a fta:failure-rate-estimate;
                                                         fta:value ?fhaBasedFailureRate.
                                    } 
                                    OPTIONAL{
                                        ?rootEventType fta:is-manifestation-of ?behavior .
                                        ?behavior fta:has-component ?_subsystemUri.
                                        ?_subsystemUri fta:is-part-of* ?systemUri.
                                        FILTER NOT EXISTS{
                                          ?systemUri fta:is-part-of ?system2.
                                        }
                                        ?systemUri fta:name ?systemName.
                                        
                                        OPTIONAL{
                                            FILTER(?systemUri != ?_subsystemUri)
                                            BIND(?_subsystemUri as ?subsystemUri)
                                            ?subsystemUri fta:is-derived-from ?subsystemType.
                                            ?subsystemType fta:name ?subsystemTypeLabel.
                                            ?subsystemType fta:ata-code ?subsystemTypeCode.
                                            BIND(CONCAT(str(?subsystemTypeCode), " - ", str(?subsystemTypeLabel)) as ?subsystemName)
                                            ?subsystemUri fta:is-part-of+ ?systemUri.
                                        }
                                    }
                                }
                            }""", "FaultTreeSummary")
                    .setParameter("type", typeUri)
                    .setParameter("pName", P_HAS_NAME)
                    .setParameter("pDescription", P_HAS_DESCRIPTION)
                    .setParameter("pCreated", P_CREATED)
                    .setParameter("pModified", P_MODIFIED)
                    .setParameter("pCreator", P_CREATOR)
                    .setParameter("pLastEditor", P_LAST_EDITOR);
    }

    public void updateStatus(URI faultTree, Status status){
        super.addOrReplaceValue(faultTree, STATUS_PROP, status, faultTree);
    }
}
