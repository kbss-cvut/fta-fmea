package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.model.HasAuthorData;
import cz.cvut.kbss.analysis.model.User;

import java.util.List;

/**
 * Base interface for data access objects having Author information.
 *
 * @param <T> Type managed by this DAO
 */
public interface AuthoredGenericDao<T extends HasAuthorData> extends GenericDao<T>{

    /**
     * Finds all entities created by given user.
     *
     * @param user Author of the data
     * @return {@code Optional} containing the matching entity instance or an empty {@code Optional}
     *     if no such instance exists
     */
    List<T> findAllForUser(User user);

}
