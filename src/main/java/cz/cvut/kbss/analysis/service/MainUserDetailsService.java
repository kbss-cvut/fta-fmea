package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class MainUserDetailsService implements UserDetailsService {

    private final UserRepositoryService userRepositoryService;

    @Autowired
    public MainUserDetailsService(UserRepositoryService userRepositoryService) {
        this.userRepositoryService = userRepositoryService;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepositoryService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found"));
    }
}
