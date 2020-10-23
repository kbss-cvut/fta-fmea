package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.Function;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.service.ComponentRepositoryService;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/components")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ComponentController {

    private final ComponentRepositoryService repositoryService;
    private final IdentifierService identifierService;

    @GetMapping
    public List<Component> findAll(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return repositoryService.findAllForUser(user);
    }

    // TODO start returning persisted entity?
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public Component createComponent(@RequestBody Component component) {
        repositoryService.persist(component);
        return component;
    }

    @GetMapping(value = "/{componentFragment}/functions", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public Set<Function> getFunctions(@PathVariable(name = "componentFragment") String componentFragment) {
        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_Component, componentFragment);
        return repositoryService.getFunctions(componentUri);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{componentFragment}/functions", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public ResponseEntity<Void> addFunction(@PathVariable(name = "componentFragment") String componentFragment, @RequestBody Function function) {
        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_Component, componentFragment);

        URI functionUri = repositoryService.addFunction(componentUri, function);
        return ResponseEntity.created(functionUri).build();
    }

}
