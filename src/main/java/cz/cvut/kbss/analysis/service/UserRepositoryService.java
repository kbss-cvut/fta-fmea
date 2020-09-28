package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.UserDao;
import cz.cvut.kbss.analysis.exception.UsernameNotAvailableException;
import cz.cvut.kbss.analysis.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Optional;

@Slf4j
@Service
public class UserRepositoryService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserRepositoryService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public URI register(User user) {
        if (userDao.exists(user.getUsername())) {
            log.warn("User with username {} already exists", user.getUsername());
            throw new UsernameNotAvailableException("Username '" + user.getUsername() + "' has already been used.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // TODO define user roles?
        user.getRoles().add("ROLE_USER");
        userDao.persist(user);

        return user.getUri();
    }

    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

}
