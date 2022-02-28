package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.dto.UserUpdateDTO;
import cz.cvut.kbss.analysis.dto.authentication.AuthenticationRequest;
import cz.cvut.kbss.analysis.dto.authentication.AuthenticationResponse;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.service.JwtTokenProvider;
import cz.cvut.kbss.analysis.service.UserRepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepositoryService userRepositoryService;


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
