package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.dto.UserUpdateDTO;
import cz.cvut.kbss.analysis.dto.authentication.AuthenticationRequest;
import cz.cvut.kbss.analysis.dto.authentication.AuthenticationResponse;
import cz.cvut.kbss.analysis.model.Roles;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.service.JwtTokenProvider;
import cz.cvut.kbss.analysis.service.UserRepositoryService;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepositoryService userRepositoryService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/register", consumes = {JsonLd.MEDIA_TYPE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> register(@RequestBody User user) {
        log.info("> register - {}", user.getUsername());
        URI uri = userRepositoryService.register(user);

        log.info("< register - {}", uri);
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/signin")
    public AuthenticationResponse signIn(@RequestBody AuthenticationRequest data) {
        log.info("> signIn - {}", data.getUsername());

        String username = data.getUsername();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, data.getPassword()));

        User user = userRepositoryService
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + "not found"));

        String token = jwtTokenProvider.createToken(username, user.getRoles());

        log.info("< signIn - {}", username);
        return new AuthenticationResponse(user.getUri(), username, token, user.getRoles().toArray(new String[0]));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/current", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public void updateCurrent(@RequestBody UserUpdateDTO userUpdate) {
        userRepositoryService.updateCurrent(userUpdate);
        log.info("< updateCurrent - user {} updated", userUpdate.getUri());
    }

}
