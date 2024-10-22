package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.config.conf.SecurityConf;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.security.SecurityConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MainUserDetailsService implements UserDetailsService {

    private final UserRepositoryService userRepositoryService;

    private final SecurityConf securityConf;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepositoryService.findByUsername(username)
                .map(this::setDefaultRoles)
                .orElseThrow(() -> new UsernameNotFoundException("Username: " + username + " not found"));
    }

    /**
     * Adds default roles to user based on security provider. Should be applied to authenticated users.
     *
     * <p>Default roles based on security provider:</p>
     * <ul>
     * <li> internal - add default role "user"</li>
     * <li> oidc - no default roles are added.</li>
     * </ul>
     * @param user should be authenticated user
     * @return input user with default roles
     */
    private User setDefaultRoles(User user){
        if(user == null || Optional.ofNullable(securityConf.getProvider())
                .filter(p -> p.equals(SecurityConstants.SEC_PROVIDER_INTERNAL)).isEmpty())
            return user;
        user.setRoles(user.getRoles() != null ? new ArrayList<>(user.getRoles()) : new ArrayList<>());
        user.getRoles().add(securityConf.getRolePrefix()+SecurityConstants.ROLE_USER);
        return user;
    }
}
