package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.service.UserRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private final UserRepositoryService repositoryService;

    @GetMapping("/current")
    public User currentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return repositoryService.getCurrent(userDetails);
    }

}
