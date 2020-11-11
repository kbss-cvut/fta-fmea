package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MainUserDetailsService implements UserDetailsService {

    private final UserRepositoryService userRepositoryService;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepositoryService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found"));
    }
}
