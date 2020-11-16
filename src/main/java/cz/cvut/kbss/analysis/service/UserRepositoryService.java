package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.UserDao;
import cz.cvut.kbss.analysis.dto.UserUpdateDTO;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.exception.LogicViolationException;
import cz.cvut.kbss.analysis.exception.UsernameNotAvailableException;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.service.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserRepositoryService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public User getCurrent(UserDetails userDetails) {
        return userDao
                .findByUsername(userDetails.getUsername())
                .orElseThrow(() ->
                        new EntityNotFoundException("Failed to find user with username - " + userDetails.getUsername())
                );
    }

    @Transactional
    public URI register(User user) {
        if (userDao.exists(user.getUsername())) {
            log.warn("User with username {} already exists", user.getUsername());
            throw new UsernameNotAvailableException("Username '" + user.getUsername() + "' has already been used.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.persist(user);

        return user.getUri();
    }

    @Transactional
    public void updateCurrent(UserUpdateDTO userUpdate) {
        log.info("> updateCurrent - {}", userUpdate.getUsername());

        User currentUser = SecurityUtils.currentUser();
        if(!currentUser.getUri().equals(userUpdate.getUri())) {
            log.warn("< updateCurrent - URIs do not match! {} != {}", currentUser.getUri(), userUpdate.getUri());
            throw new LogicViolationException("User update uri does not match current user!");
        }

        if(!passwordEncoder.matches(userUpdate.getPassword(), currentUser.getPassword())) {
            log.warn("< updateCurrent - Old password incorrect!");
            throw new LogicViolationException("Old password incorrect!");
        }

        User user = userUpdate.asUser();
        user.setPassword(passwordEncoder.encode(userUpdate.getNewPassword()));

        userDao.update(user);

        log.info("< updateCurrent - user successfully updated");
    }

    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }
}
