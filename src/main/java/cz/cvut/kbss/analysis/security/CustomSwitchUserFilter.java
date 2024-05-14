package cz.cvut.kbss.analysis.security;

import cz.cvut.kbss.analysis.exception.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Extends default user switching logic by preventing switching to an admin account.
 */
public class CustomSwitchUserFilter extends SwitchUserFilter {

    @Override
    protected Authentication attemptSwitchUser(HttpServletRequest request) throws AuthenticationException {
        final Authentication switchTo = super.attemptSwitchUser(request);
        if (switchTo.getAuthorities().stream().anyMatch(a -> SecurityConstants.ROLE_ADMIN.equals(a.getAuthority()))) {
            throw new BadRequestException("Cannot impersonate admin.");
        }
        return switchTo;
    }
}