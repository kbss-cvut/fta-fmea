package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.service.ComponentRepositoryService;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/components")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ComponentController {

    private final ComponentRepositoryService repositoryService;

    // TODO query only the ones for logged in user

    @GetMapping("/")
    public List<Component> findAll() {
        return repositoryService.findAll();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public URI createComponent(@RequestBody Component component) {
        return repositoryService.persist(component);
    }

}
