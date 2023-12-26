package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.security.SecurityConstants;
import cz.cvut.kbss.analysis.service.UserRepositoryService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API for getting basic user info.
 * <p>
 * Enabled when OIDC security is used.
 */
@ConditionalOnProperty(prefix = "security", name = "provider", havingValue = "oidc")
@RestController
@RequestMapping("/users")
public class OidcUserController {

    private final UserRepositoryService userService;


    public OidcUserController(UserRepositoryService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('" + SecurityConstants.ROLE_USER + "')")
    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getCurrent() {
        return userService.getCurrentUser();
    }

    @PreAuthorize("hasRole('" + SecurityConstants.ROLE_ADMIN + "') or #username == authentication.name or " +
            "hasRole('" + SecurityConstants.ROLE_USER + "')")
    @GetMapping(value = "/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getByUsername(@PathVariable("username") String username) {
        return userService.findByUsername(username).orElseThrow(() ->
                 EntityNotFoundException.create("User", username));
    }


}