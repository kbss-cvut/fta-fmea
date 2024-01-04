package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.exception.PersistenceException;
import cz.cvut.kbss.analysis.model.util.EntityToOwlClassMapper;
import cz.cvut.kbss.analysis.model.util.HasIdentifier;
import cz.cvut.kbss.jopa.model.EntityManager;

import java.net.URI;
import java.util.*;

/**
 * Base implementation of the generic DAO API.
 *
 * @param <T> the entity class this DAO manages
 */
public abstract class BaseDao<T extends HasIdentifier> implements GenericDao<T> {

    protected final Class<T> type;
    protected final URI typeUri;

    protected final EntityManager em;
    protected final PersistenceConf config;

    protected BaseDao(Class<T> type, EntityManager em, PersistenceConf config) {
        this.type = type;
        this.typeUri = URI.create(EntityToOwlClassMapper.getOwlClassForEntity(type));
        this.em = em;
        this.config = config;
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
            return Optional.ofNullable(em.find(type, id));
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public Optional<T> getReference(URI id) {
        Objects.requireNonNull(id);
        try {
            return Optional.ofNullable(em.getReference(type, id));
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void persist(T entity) {
        Objects.requireNonNull(entity);
        try {
            em.persist(entity);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void persist(Collection<T> entities) {
        Objects.requireNonNull(entities);
        try {
            entities.forEach(em::persist);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public T update(T entity) {
        Objects.requireNonNull(entity);
        try {
            return em.merge(entity);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void remove(T entity) {
        Objects.requireNonNull(entity);
        try {
            em.remove(em.merge(entity));
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

    @Override
    public boolean existsInContext(URI id) {
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