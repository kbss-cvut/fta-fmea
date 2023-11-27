package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;
import java.util.UUID;

@ContextConfiguration(classes = {UserDao.class})
class UserDaoTest extends BaseDaoTestRunner {

    @Autowired
    private EntityManager em;

    @Autowired
    private UserDao userDao;

    @Test
    public void findByUsername_userNotExists_shouldReturnNull() {
        String username = "unknownUsername";
        Optional<User> result = userDao.findByUsername(username);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void findByUsername_userExists_shouldFindUser() {
        User user = new User();
        user.setUsername("testUsername");
        user.setPassword(UUID.randomUUID().toString());

        transactional(() -> em.persist(user));

        Optional<User> result = userDao.findByUsername(user.getUsername());
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(user.getUsername(), result.get().getUsername());
    }

    @Test
    public void existsWithUsername_userNotExists_shouldReturnFalse() {
        String username = "unknownUsername";
        boolean result = userDao.existsWithUsername(username);
        Assertions.assertFalse(result);
    }

    @Test
    public void existsWithUsername_userExists_shouldReturnTrue() {
        User user = new User();
        user.setUsername("testUsername");
        user.setPassword(UUID.randomUUID().toString());

        transactional(() -> em.persist(user));

        boolean result = userDao.existsWithUsername(user.getUsername());
        Assertions.assertTrue(result);
    }

}