package cz.cvut.kbss.analysis.security;

import cz.cvut.kbss.analysis.exception.BadCredentialsException;
import cz.cvut.kbss.analysis.service.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * The class is used for local authentication instead of OAuth2.
 */
@Service
@Slf4j
public class OntologyAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    public OntologyAuthenticationProvider(UserDetailsService userDetailsService,
                                          PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = authentication.getPrincipal().toString();
        log.atDebug().log("Authenticating user {}", username);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final String password = (String) authentication.getCredentials();
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            log.trace("Provided password for username '{}' doesn't match.", username);
            throw new BadCredentialsException("error.user.passwordMismatch" ,"Provided password for username '" + username + "' doesn't match.", Map.of("username", username));
        }
        return SecurityUtils.setCurrentUser(userDetails);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
