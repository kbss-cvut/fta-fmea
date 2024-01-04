package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.exception.ResourceException;
import cz.cvut.kbss.analysis.model.Behavior;
import cz.cvut.kbss.analysis.resources.ResourceUtils;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.IRI;
import org.eclipse.rdf4j.common.io.ResourceUtil;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public abstract class BehaviorDao<T extends Behavior> extends NamedEntityDao<T>{
    public BehaviorDao(Class<T> type, EntityManager em, PersistenceConf config) {
        super(type, em, config);
    }

    public Set<URI> getTransitiveRequiredBehaviors(URI behaviorURI){
        return getTransitiveClosure(ResourceUtils.REQUIRED_BEHAVIORS, behaviorURI);
    }

    public Set<URI> getTransitiveBehaviorParts(URI behaviorURI){
        return getTransitiveClosure(ResourceUtils.BEHAVIOR_PARTS, behaviorURI);
    }

    public Set<URI> getTransitiveImpairingBehaviors(URI behaviorURI){
        return getTransitiveClosure(ResourceUtils.IMPAIRING_BEHAVIORS, URI.create(Vocabulary.s_p_impairs));
    }

    /**
     * Retrieves indirectly required behaviors
     * @param behaviorURI
     * @return
     */
    public Set<URI> getIndirectRequiredBehaviors(URI behaviorURI){
        return getTransitiveClosure(ResourceUtils.INDIRECT_REQUIRED_BEHAVIORS, behaviorURI);
    }

    /**
     * Retrieves indirect behavior parts
     * @param behaviorURI
     * @return
     */
    public Set<URI> getIndirectBehaviorParts(URI behaviorURI){
        return getTransitiveClosure(ResourceUtils.INDIRECT_BEHAVIOR_PARTS, behaviorURI);
    }


    /**
     * Retrieves indirect impairing behaviors
     * @param behaviorURI
     * @return
     */
    public Set<URI> getIndirectImpairingBehaviors(URI behaviorURI){
        return getTransitiveClosure(ResourceUtils.INDIRECT_IMPAIRING_BEHAVIORS, URI.create(Vocabulary.s_p_impairs));
    }

    public Set<URI> getTransitiveClosure(String queryName, URI behaviorURI) {
        try {
            String query = ResourceUtils.loadQuery(queryName);
            Set<URI> transitiveClosure = em.createNativeQuery(query, URI.class)
                    .setParameter("behavior", behaviorURI)
                    .setParameter("behaviorType", em.getMetamodel().entity(type).getIRI().toURI())
                    .getResultStream().collect(Collectors.toSet());
            return transitiveClosure;
        } catch (IOException e) {
            throw new ResourceException(e);
        }
    }
}
