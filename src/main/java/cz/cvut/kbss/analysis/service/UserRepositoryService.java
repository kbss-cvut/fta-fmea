package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.dao.UserDao;
import cz.cvut.kbss.analysis.dto.UserUpdateDTO;
import cz.cvut.kbss.analysis.exception.LogicViolationException;
import cz.cvut.kbss.analysis.exception.UsernameNotAvailableException;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.service.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.net.URI;
import java.util.Optional;

@Slf4j
@Service
public class UserRepositoryService extends BaseRepositoryService<User> {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final SecurityUtils securityUtils;

    @Autowired
    public UserRepositoryService(@Qualifier("defaultValidator") Validator validator, UserDao userDao, PasswordEncoder passwordEncoder, SecurityUtils securityUtils) {
        super(validator);
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.securityUtils = securityUtils;
    }

    public User getCurrentUser() {
        return securityUtils.getCurrentUser();
    }

    @Override
    protected GenericDao<User> getPrimaryDao() {
        return userDao;
    }

    @Transactional
    public URI register(User user) {
        if (userDao.existsWithUsername(user.getUsername())) {
            log.warn("User with username {} already exists", user.getUsername());
            throw new UsernameNotAvailableException("Username '" + user.getUsername() + "' has already been used.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        persist(user);

        return user.getUri();
    }

    @Transactional
    public void updateCurrent(UserUpdateDTO userUpdate) {
        log.info("> updateCurrent - {}", userUpdate.getUsername());

        User currentUser = getCurrentUser();
        if (!currentUser.getUri().equals(userUpdate.getUri())) {
            log.warn("< updateCurrent - URIs do not match! {} != {}", currentUser.getUri(), userUpdate.getUri());
            throw new LogicViolationException("User update uri does not match current user!");
        }

        if (!passwordEncoder.matches(userUpdate.getPassword(), currentUser.getPassword())) {
            log.warn("< updateCurrent - Old password incorrect!");
            throw new LogicViolationException("Old password incorrect!");
        }

        User user = userUpdate.asUser();
        user.setPassword(passwordEncoder.encode(userUpdate.getNewPassword()));

        update(user);

        log.info("< updateCurrent - user successfully updated");
    }

    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

}
