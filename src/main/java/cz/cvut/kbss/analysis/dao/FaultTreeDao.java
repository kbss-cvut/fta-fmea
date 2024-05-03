package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.exception.PersistenceException;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.FaultTree;
import cz.cvut.kbss.analysis.model.FaultTreeSummary;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.service.security.SecurityUtils;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import cz.cvut.kbss.jopa.model.metamodel.Attribute;
import cz.cvut.kbss.jopa.model.metamodel.EntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Repository
public class FaultTreeDao extends ManagedEntityDao<FaultTree> {

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
        EntityType<FaultEvent> fe = em.getMetamodel().entity(FaultEvent.class);
        Attribute manifestingEvent = ft.getAttribute("manifestingEvent");
        Attribute children = fe.getAttribute("children");

        entityDescriptor.addAttributeContext(manifestingEvent, uri);
        entityDescriptor.getAttributeDescriptor(manifestingEvent)
                .addAttributeContext(fe.getAttribute("supertypes"), null)
                .addAttributeContext(children, uri).getAttributeDescriptor(children)
                        .addAttributeContext(fe.getAttribute("supertypes"), null);

        entityDescriptor.addAttributeContext(ft.getAttribute("failureModesTable"), null);

        return entityDescriptor;
    }

    @Override
    public Optional<FaultTree> find(URI id) {
        Optional<FaultTree> faultTreeOpt = super.find(id);
        if(faultTreeOpt.isEmpty())
            return faultTreeOpt;
        FaultTree faultTree = faultTreeOpt.get();
        faultTree.getAllEvents().stream()
                .map(e -> e.getBehavior()).filter(b -> b!=null).map(b -> b.getItem())
                .filter(i -> i != null).forEach(i -> i.getName());

        return Optional.of(faultTree);
    }

    @Override
    public List<FaultTree> findAllSummaries() {
        try {
            List<FaultTreeSummary> ret = em.createNativeQuery("""
                            PREFIX fta: <http://onto.fel.cvut.cz/ontologies/fta-fmea-application/>
                            SELECT * WHERE { 
                                ?uri a ?type. 
                                ?uri ?pName ?name.
                                OPTIONAL{?uri ?pDescription ?description.} 
                                OPTIONAL{?uri ?pCreated ?created.}
                                OPTIONAL{?uri ?pModified ?modified.}
                                OPTIONAL{?uri ?pCreator ?creator.}
                                OPTIONAL{?uri ?pLastEditor ?lastEditor.}
                                OPTIONAL{ 
                                    ?uri fta:is-manifested-by ?root .
                                    ?root fta:is-derived-from ?sup.
                                    OPTIONAL{
                                        ?root fta:probability ?calculatedFailureRate.
                                    }
                                    OPTIONAL{
                                        ?sup fta:has-failure-rate ?failureRate.
                                        OPTIONAL{
                                            ?failureRate fta:has-prediction ?failureRatePrediction.
                                            ?failureRatePrediction fta:value ?fhaBasedFailureRate.
                                        }
                                        
                                        OPTIONAL{
                                            ?failureRate fta:has-requirement ?failureRateRequirement.
                                            ?failureRateRequirement fta:to ?requiredFailureRate.
                                        }
                                    }
                                    OPTIONAL{
                                        ?sup fta:is-manifestation-of ?behavior .
                                        ?behavior fta:has-component ?subsystemUri.
                                        ?subsystemUri fta:name ?subsystemName.
                                        ?subsystemUri fta:is-part-of+ ?systemUri.
                                        FILTER NOT EXISTS{
                                            ?systemUri fta:is-part-of ?system2.
                                        }
                                        ?systemUri fta:name ?systemName.
                                    }
                                }
                                
                                {}
                            }""", "FaultTreeSummary")
                    .setParameter("type", typeUri)
                    .setParameter("pName", P_HAS_NAME)
                    .setParameter("pDescription", P_HAS_DESCRIPTION)
                    .setParameter("pCreated", P_CREATED)
                    .setParameter("pModified", P_MODIFIED)
                    .setParameter("pCreator", P_CREATOR)
                    .setParameter("pLastEditor", P_LAST_EDITOR)
                    .getResultList();

            return ret.stream().map(s -> s.asEntity(type)).toList();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }
}
