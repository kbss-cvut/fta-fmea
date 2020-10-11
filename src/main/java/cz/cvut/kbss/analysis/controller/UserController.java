package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.service.UserRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepositoryService userRepositoryService;

    @Autowired
    public UserController(UserRepositoryService userRepositoryService) {
        this.userRepositoryService = userRepositoryService;
    }

    @GetMapping("/current")
    public User currentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userRepositoryService.getCurrent(userDetails);
    }

}
