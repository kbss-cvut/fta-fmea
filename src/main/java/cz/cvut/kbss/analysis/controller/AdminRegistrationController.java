package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.service.UserRepositoryService;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//@Slf4j
//@Profile("admin-registration-only")
public class AdminRegistrationController {

//    private final UserRepositoryService userRepositoryService;
//
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @ResponseStatus(HttpStatus.CREATED)
//    @PostMapping(value = "/register", consumes = {JsonLd.MEDIA_TYPE}, produces = {MediaType.APPLICATION_JSON_VALUE})
//    public ResponseEntity<Void> register(@RequestBody User user) {
//        log.info("> register - {}", user.getUsername());
//        URI uri = userRepositoryService.register(user);
//
//        log.info("< register - {}", uri);
//        return ResponseEntity.created(uri).build();
//    }
}
