package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.service.ComponentRepositoryService;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/components")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ComponentController {

    private final ComponentRepositoryService repositoryService;

    @GetMapping
    public List<Component> findAll(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return repositoryService.findAllForUser(user);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public URI createComponent(@RequestBody Component component) {
        return repositoryService.persist(component);
    }

}
