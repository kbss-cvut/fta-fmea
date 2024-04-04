package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.model.util.HasIdentifier;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Base interface for data access objects.
 *
 * @param <T> Type managed by this DAO
 */
public interface GenericDao<T extends HasIdentifier> {

    /**
     * Finds all instances of the class managed by this DAO.
     *
     * @return All known instances
     */
    List<T> findAll();

    /**
     * Finds all instances of the class managed by this DAO stored in a particular context.
     *
     * @return All known instances
     */
    List<T> findAll(URI context);

    /**
     * Finds entity instance with the specified identifier.
     *
     * @param id Identifier
     * @return {@code Optional} containing the matching entity instance or an empty {@code Optional}
     *     if no such instance exists
     */
    Optional<T> find(URI id);

    /**
     * Gets a reference to an instance with the specified identifier.
     *
     * <p>Note that the reference is initially an empty object wth all attributes loaded lazily and
     * the corresponding persistence context has to be available for the loading. This method should
     * be useful for removal and update operations.
     *
     * @param id Identifier
     * @return {@code Optional} containing a reference to a matching instance or an empty {@code
     *     Optional }if no such instance exists
     */
    Optional<T> getReference(URI id);

    /**
     * Persists the specified entity.
     *
     * @param entity Entity to persist
     */
    void persist(T entity);

    /**
     * Persists the specified instances.
     *
     * @param entities Entities to persist
     */
    void persist(Collection<T> entities);

    /**
     * Updates the specified entity.
     *
     * @param entity Entity to update
     * @return The updated entity. Use it for further processing, as it could be a completely
     *     different instance
     */
    T update(T entity);

    /**
     * Removes the specified entity.
     *
     * @param entity Entity to remove
     */
    void remove(T entity);

    /**
     * Removes an entity with the specified id.
     *
     * @param id Entity identifier
     */
    void remove(URI id);

    /**
     * Checks whether an entity with the specified id exists (and has the type managed by this
     * DAO).
     *
     * @param id Entity identifier
     * @return {@literal true} if entity exists, {@literal false} otherwise
     */
    boolean exists(URI id);

    /**
     * Checks whether an entity with the specified id exists in the current context including the repository (and has
     * the type managed by this DAO).
     *
     * @param id Entity identifier
     * @return {@literal true} if entity exists, {@literal false} otherwise
     */
    boolean existsInPersistenceContext(URI id);

    /**
     * Checks whether an entity with the specified predicate and its value exists (and has the type managed by this
     * DAO).
     *
     * @param predicate Predicate of the subject
     * @param value Value to match
     * @return {@literal true} if entity exists, {@literal false} otherwise
     */
    boolean existsWithPredicate(String predicate, String value);
}
