package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.UserDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.exception.UsernameNotAvailableException;
import cz.cvut.kbss.analysis.model.User;
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

    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

}
