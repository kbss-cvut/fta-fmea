package cz.cvut.kbss.analysis.service.security;

import cz.cvut.kbss.analysis.config.conf.SecurityConf;
import cz.cvut.kbss.analysis.dao.UserDao;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.util.OidcGrantedAuthoritiesExtractor;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handle user session-related functions.
 */
@Service
@AllArgsConstructor
public class SecurityUtils {

    private final UserDao userDao;

    private final SecurityConf config;

//    public SecurityUtils() {
//        // Ensures security context is propagated to additionally spun threads, e.g., used
//        // by @Async methods
//        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
//    }

    /**
     * <p>It allows to access the currently logged-in user without injecting {@code SecurityUtils}
     * as a bean.
     *
     * @return Currently logged-in user
     */
    public static User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Gets the currently authenticated user.
     * If the user is impersonating another user, the impersonated user is returned.
     * Otherwise, the currently authenticated user is returned.
     *
     * @return
     */
    public User getCurrentUser() {
        final SecurityContext context = SecurityContextHolder.getContext();
        assert context != null;
        final Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof Jwt) {
            return resolveAccountFromOAuthPrincipal((Jwt) principal);
        } else {
            final String username = context.getAuthentication().getName();
            final User user = userDao.findByUsername(username).orElseThrow().copy();
            //TODO impersonalization?
//            if (context.getAuthentication().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(
//                    SwitchUserWebFilter.ROLE_PREVIOUS_ADMINISTRATOR))) {
//                user.addType(Vocabulary.s_c_impersonator);
//            }
            return user;
        }
    }

    private User resolveAccountFromOAuthPrincipal(Jwt principal) {
        final OidcUserInfo userInfo = new OidcUserInfo(principal.getClaims());
        final List<String> roles = new OidcGrantedAuthoritiesExtractor(config).extractRoles(principal);
        final User user = userDao.findByUsername(userInfo.getPreferredUsername()).orElseThrow(); // TODO throw EntityNotFoundException
//        TODO resolve role
//        roles.stream().map(Role::forName).filter(Optional::isPresent).forEach(r -> user.addType(r.get().getType()));
        return user;
    }


//    /**
//     * Sets authentication to the current thread's security context.
//     *
//     * @param authentication Currently logged-in user's authentication
//     */
//    public static void setCurrentUser(Authentication authentication) {
//        final SecurityContext context = new SecurityContextImpl();
//        context.setAuthentication(authentication);
//        SecurityContextHolder.setContext(context);
//    }
    /**
     * Sets the current security context to the user represented by the provided user details.
     * <p>
     * Note that this method erases credentials from the provided user details for security reasons.
     * <p>
     * This method should be used only when internal authentication is used.
     *
     * @param userDetails User details
     */
    public static AbstractAuthenticationToken setCurrentUser(UserDetails userDetails) {
        final UsernamePasswordAuthenticationToken token =
                UsernamePasswordAuthenticationToken.authenticated(userDetails, userDetails.getPassword(),
                        userDetails.getAuthorities());
        token.setDetails(userDetails);
        token.eraseCredentials();   // Do not pass credentials around

        final SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(token);
        SecurityContextHolder.setContext(context);
        return token;
    }


}

