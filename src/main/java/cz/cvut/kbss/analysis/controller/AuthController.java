package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.dto.authentication.AuthenticationRequest;
import cz.cvut.kbss.analysis.dto.authentication.AuthenticationResponse;
import cz.cvut.kbss.analysis.dto.registration.UserRegistrationRequest;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.service.JwtTokenProvider;
import cz.cvut.kbss.analysis.service.UserRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepositoryService userRepositoryService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserRepositoryService userRepositoryService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepositoryService = userRepositoryService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/register", produces = {MediaType.APPLICATION_JSON_VALUE})
    public URI register(@RequestBody UserRegistrationRequest registrationRequest) {
        User newUser = new User();
        newUser.setUsername(registrationRequest.getUsername());
        newUser.setPassword(registrationRequest.getPassword());

        return userRepositoryService.register(newUser);
    }

    @PostMapping("/signin")
    public AuthenticationResponse signIn(@RequestBody AuthenticationRequest data) {
        String username = data.getUsername();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
        List<String> userRoles = userRepositoryService
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found"))
                .getRoles();
        String token = jwtTokenProvider.createToken(username, userRoles);

        return new AuthenticationResponse(username, token);
    }

}
