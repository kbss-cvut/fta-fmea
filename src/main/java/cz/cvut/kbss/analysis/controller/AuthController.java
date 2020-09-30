package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.dto.AuthenticationRequest;
import cz.cvut.kbss.analysis.dto.UserRegistrationRequest;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.service.JwtTokenProvider;
import cz.cvut.kbss.analysis.service.UserRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

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

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody UserRegistrationRequest registrationRequest) {
        User newUser = new User();
        newUser.setUsername(registrationRequest.getUsername());
        newUser.setPassword(registrationRequest.getPassword());

        URI userUri = userRepositoryService.register(newUser);

        // TODO user regular mapping
        Map<Object, Object> model = new HashMap<>();
        model.put("uri", userUri);
        return ok(model);
    }

    @PostMapping("/signin")
    public ResponseEntity signIn(@RequestBody AuthenticationRequest data) {
        try {
            String username = data.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));
            List<String> userRoles = userRepositoryService
                    .findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found"))
                    .getRoles();
            String token = jwtTokenProvider.createToken(username, userRoles);

            Map<Object, Object> model = new HashMap<>();
            model.put("username", username);
            model.put("token", token);
            return ok(model);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password supplied");
        }
    }

}
