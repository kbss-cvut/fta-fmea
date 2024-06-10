package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.exception.PersistenceException;
import cz.cvut.kbss.analysis.model.AbstractEntity;
import cz.cvut.kbss.analysis.model.util.EntityToOwlClassMapper;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Base implementation of the generic DAO API.
 *
 * @param <T> the entity class this DAO manages
 */
public abstract class BaseDao<T extends AbstractEntity> implements GenericDao<T> {

    protected final Class<T> type;
    protected final URI typeUri;

    protected final EntityManager em;
    protected final PersistenceConf config;
    protected final IdentifierService identifierService;

    protected BaseDao(Class<T> type, EntityManager em, PersistenceConf config, IdentifierService identifierService) {
        this.type = type;
        this.typeUri = URI.create(EntityToOwlClassMapper.getOwlClassForEntity(type));
        this.em = em;
        this.config = config;
        this.identifierService = identifierService;
    }

    public EntityDescriptor getEntityDescriptor(T entity){
        EntityDescriptor descriptor = new EntityDescriptor(entity.getContext());
        setEntityDescriptor(descriptor);
        return descriptor;
    }

    public EntityDescriptor getEntityDescriptor(URI uri){
        EntityDescriptor descriptor = new EntityDescriptor();
        setEntityDescriptor(descriptor);
        return descriptor;
    }

    protected void setEntityDescriptor(EntityDescriptor descriptor){

    }

    public URI getContext(T entity){
        if(entity.getContext() == null)
            entity.setContext(getContext(entity.getUri()));
        return entity.getContext();
    }

    public void deleteContext(URI context){
        Objects.requireNonNull(context);
        em.createNativeQuery("DELETE {GRAPH ?context { ?s ?p ?o}} WHERE {GRAPH ?context { ?s ?p ?o}}")
                .setParameter("context", context)
                .executeUpdate();
    }

    public URI getContext(URI uri){
        List<URI> contexts = em.createNativeQuery("SELECT DISTINCT ?context {GRAPH ?context {?uri a ?type}}", URI.class)
                .setParameter("uri", uri)
                .getResultList();
        if(contexts.isEmpty())
            return null;
        return contexts.get(0);
    }

    @Override
    public List<T> findAll() {
        try {
            return em.createNativeQuery("SELECT ?x WHERE { ?x a ?type . }", type)
                    .setParameter("type", typeUri)
                    .getResultList();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public List<T> findAll(URI contenxt) {
        try {
            return em.createNativeQuery("SELECT ?x WHERE { GRAPH ?context{ ?x a ?type .} }", type)
                    .setParameter("type", typeUri)
                    .setParameter("context", contenxt)
                    .getResultList();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public Optional<T> find(URI id) {
        Objects.requireNonNull(id);
        try {
            EntityDescriptor entityDescriptor = getEntityDescriptor(id);
            return Optional.ofNullable(em.find(type, id, entityDescriptor));
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public Optional<T> getReference(URI id) {
        Objects.requireNonNull(id);
        try {
            EntityDescriptor entityDescriptor = getEntityDescriptor(id);
            return Optional.ofNullable(em.getReference(type, id, entityDescriptor));
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void persist(T entity) {
        Objects.requireNonNull(entity);
        try {
            _persist(entity);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void persist(Collection<T> entities) {
        Objects.requireNonNull(entities);
        try {
            entities.forEach(this::_persist);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    protected void _persist(T entity){
        EntityDescriptor entityDescriptor = getEntityDescriptor(entity);
        em.persist(entity, entityDescriptor);
    }

    @Override
    public T update(T entity) {
        Objects.requireNonNull(entity);
        try {
            EntityDescriptor entityDescriptor = getEntityDescriptor(entity);
            return em.merge(entity, entityDescriptor);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void remove(T entity) {
        Objects.requireNonNull(entity);
        try {
            EntityDescriptor entityDescriptor = getEntityDescriptor(entity);
            em.remove(em.merge(entity,entityDescriptor));
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void remove(URI id) {
        Objects.requireNonNull(id);
        try {
            find(id).ifPresent(em::remove);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public boolean exists(URI id) {
        Objects.requireNonNull(id);
        try {
            return em.createNativeQuery("ASK { ?x a ?type . }", Boolean.class)
                    .setParameter("x", id)
                    .setParameter("type", typeUri)
                    .getSingleResult();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Checks if the input id exists in current persistence context.
     * @param id Entity identifier
     * @return
     */
    @Override
    public boolean existsInPersistenceContext(URI id) {
        Objects.requireNonNull(id);
        return em.find(type, id) != null;
    }

    @Override
    public boolean existsWithPredicate(String predicate, String value) {
        Objects.requireNonNull(value);
        return em
                .createNativeQuery("ASK WHERE { ?x a ?type ; ?predicate ?value . }", Boolean.class)
                .setParameter("type", typeUri)
                .setParameter("predicate", URI.create(predicate))
                .setParameter("value", value, config.getLanguage())
                .getSingleResult();
    }

}