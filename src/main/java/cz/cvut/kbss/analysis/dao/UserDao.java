package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.exception.PersistenceException;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.exceptions.NoResultException;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

@Repository
public class UserDao extends BaseDao<User> {

    @Autowired
    public UserDao(EntityManager em, PersistenceConf config) {
        super(User.class, em, config);
    }

    /**
     * Finds a user with the specified username.
     *
     * @param username Username to search by
     * @return User with matching username
     */
    public Optional<User> findByUsername(String username) {
        Objects.requireNonNull(username);
        try {
            return Optional
                    .of(em
                            .createNativeQuery("SELECT ?x WHERE { ?x a ?type ; ?hasUsername ?username . }",
                                    type)
                            .setParameter("type", typeUri)
                            .setParameter("hasUsername", URI.create(Vocabulary.s_p_hasUsername))
                            .setParameter("username", username, config.getLanguage())
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Checks whether a user with the specified username exists.
     *
     * @param username Username to check
     * @return {@code true} if a user with the specified username exists
     */
    public boolean exists(String username) {
        Objects.requireNonNull(username);
        return em
                .createNativeQuery("ASK WHERE { ?x a ?type ; ?hasUsername ?username . }", Boolean.class)
                .setParameter("type", typeUri)
                .setParameter("hasUsername", URI.create(Vocabulary.s_p_hasUsername))
                .setParameter("username", username, config.getLanguage())
                .getSingleResult();
    }

}
