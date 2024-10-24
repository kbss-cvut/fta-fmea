package cz.cvut.kbss.analysis.controller;


import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.security.SecurityConstants;
import cz.cvut.kbss.analysis.service.UserRepositoryService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ConditionalOnProperty(prefix = "security", name = "provider", havingValue = SecurityConstants.SEC_PROVIDER_INTERNAL, matchIfMissing = true)
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepositoryService userService;

    public UserController(UserRepositoryService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('" + SecurityConstants.ROLE_USER + "')")
    @GetMapping(value = "/current", produces = MediaType.APPLICATION_JSON_VALUE)
    public User getCurrent() {
        return userService.getCurrentUser();
    }

}
