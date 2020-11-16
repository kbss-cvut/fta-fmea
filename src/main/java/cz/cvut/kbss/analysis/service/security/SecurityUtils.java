package cz.cvut.kbss.analysis.service.security;

import cz.cvut.kbss.analysis.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Handle user session-related functions.
 */
@Service
public class SecurityUtils {
    
    public SecurityUtils() {
        // Ensures security context is propagated to additionally spun threads, e.g., used
        // by @Async methods
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

    /**
     *
     * <p>It allows to access the currently logged in user without injecting {@code SecurityUtils}
     * as a bean.
     *
     * @return Currently logged in user
     */
    public static User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Sets authentication to the current thread's security context.
     *
     * @param authentication Currently logged-in user's authentication
     */
    public void setCurrentUser(Authentication authentication) {
        final SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

}

