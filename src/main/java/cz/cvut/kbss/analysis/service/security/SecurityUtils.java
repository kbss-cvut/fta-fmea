package cz.cvut.kbss.analysis.service.security;

import cz.cvut.kbss.analysis.config.conf.SecurityConf;
import cz.cvut.kbss.analysis.dao.UserDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.model.UserReference;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/**
 * Handle user session-related functions.
 */
@Service
public class SecurityUtils {

    private final UserDao userDao;

    private final SecurityConf config;

    public SecurityUtils(UserDao userDao, SecurityConf config) {
        this.userDao = userDao;
        this.config = config;
        // Ensures security context is propagated to additionally spun threads, e.g., used
        // by @Async methods
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL); // TODO check what it does
    }

    /**
     * Gets the currently authenticated user.
     * If the user is impersonating another user, the impersonated user is returned.
     * Otherwise, the currently authenticated user is returned.
     *
     * @return the instance of the User class representing the currently authenticated user
     */
    public User getCurrentUser() {
        final SecurityContext context = SecurityContextHolder.getContext();
        assert context != null;
        final Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof Jwt) {
            return resolveAccountFromOAuthPrincipal((Jwt) principal);
        } else {
            final String username = context.getAuthentication().getName();
            //TODO impersonalization?
            return userDao.findByUsername(username).orElseThrow().copy();
        }
    }

    public String getCurrentUsername(){
        final SecurityContext context = SecurityContextHolder.getContext();
        assert context != null;
        final Object principal = context.getAuthentication().getPrincipal();
        if (principal instanceof Jwt) {
            final OidcUserInfo userInfo = new OidcUserInfo(((Jwt)principal).getClaims());
            return userInfo.getPreferredUsername();
        } else {
            return context.getAuthentication().getName();
        }
    }

    public UserReference getCurrentUserReference() {
        String username = getCurrentUsername();
        return userDao.findUserReferenceByUsername(username);
    }

    //    TODO map role, but I am not sure which changes in the model when be required if I add addRole method to User
    private User resolveAccountFromOAuthPrincipal(Jwt principal) {
        final OidcUserInfo userInfo = new OidcUserInfo(principal.getClaims());
//        final List<String> roles = new OidcGrantedAuthoritiesExtractor(config).extractRoles(principal);
//        var user = userDao.findByUsername(userInfo.getPreferredUsername());
//        roles.stream().map(r -> "ROLE_" + r).forEach(user::addRole);
        return userDao.findByUsername(userInfo.getPreferredUsername()).orElseThrow(() -> EntityNotFoundException.create("User", userInfo.getPreferredUsername()));
    }



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

