package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.exception.PersistenceException;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.model.UserReference;
import cz.cvut.kbss.analysis.service.IdentifierService;
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
    public UserDao(EntityManager em, PersistenceConf config, IdentifierService identifierService) {
        super(User.class, em, config, identifierService);
    }

    /**
     * Finds a user with the specified username.
     *
     * @param username Username to search by
     * @return User with matching username
     */
    public Optional<User> findByUsername(String username) {
        Objects.requireNonNull(username);
        URI userUri = findUriByUsername(username);
        return userUri == null
                ? Optional.empty()
                : find(userUri);
    }

    public UserReference findUserReferenceByUsername(String username) {
        URI uri = findUriByUsername(username);
        if(uri == null)
            return null;
        UserReference userReference = new UserReference();
        userReference.setUsername(username);
        userReference.setUri(uri);
        return userReference;
    }

    public URI findUriByUsername(String username) {
        try {
            return em.createNativeQuery("""
                                    SELECT ?x WHERE { 
                                        ?x a ?type ; ?hasUsername ?val . 
                                        FILTER(str(?val) = ?username)
                                    }
                                    """,
                        URI.class)
                .setParameter("type", typeUri)
                .setParameter("hasUsername", URI.create(Vocabulary.s_p_accountName))
                .setParameter("username", username)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    public String findUsername(URI uri){
        Objects.requireNonNull(uri);
        try {
            return em
                        .createNativeQuery("SELECT ?username WHERE { ?x a ?type ; ?hasUsername ?username . }",
                                String.class)
                        .setParameter("type", typeUri)
                        .setParameter("x", uri)
                        .setParameter("hasUsername", URI.create(Vocabulary.s_p_accountName))
                        .getSingleResult();
        } catch (NoResultException e) {
            return null;
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
    public boolean existsWithUsername(String username) {
        Objects.requireNonNull(username);
        return em
                .createNativeQuery("""
                                    ASK WHERE { 
                                        ?x a ?type ; ?hasUsername ?val . 
                                        FILTER(str(?val) = ?username)
                                    }
                                    """, Boolean.class)
                .setParameter("type", typeUri)
                .setParameter("hasUsername", URI.create(Vocabulary.s_p_accountName))
                .setParameter("username", username)
                .getSingleResult();
    }

}
