package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.model.Function;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.service.ComponentRepositoryService;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/components")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ComponentController {

    private final ComponentRepositoryService repositoryService;
    private final IdentifierService identifierService;

    @GetMapping(produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public List<Component> findAll() {
        return repositoryService.findAll();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public Component create(@RequestBody Component component) {
        log.info("> create - {}", component);
        return repositoryService.persist(component);
    }

    @GetMapping(value = "/{componentFragment}/functions", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public Set<Function> getFunctions(@PathVariable(name = "componentFragment") String componentFragment) {
        log.info("> getFunctions - {}", componentFragment);
        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_Component, componentFragment);
        return repositoryService.getFunctions(componentUri);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{componentFragment}/functions", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public Function addFunction(@PathVariable(name = "componentFragment") String componentFragment, @RequestBody Function function) {
        log.info("> addFunction - {}, {}", componentFragment, function);
        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_Component, componentFragment);

        return repositoryService.addFunction(componentUri, function);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(value = "/{componentFragment}/functions/{functionFragment}")
    public void deleteFunction(@PathVariable(name = "componentFragment") String componentFragment, @PathVariable(name = "functionFragment") String functionFragment) {
        log.info("> deleteFunction - {}, {}", componentFragment, functionFragment);
        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_Component, componentFragment);
        URI functionUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, functionFragment);

        repositoryService.deleteFunction(componentUri, functionUri);
        log.info("< deleteFunction");
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{componentFragment}/failureModes", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FailureMode addFailureMode(@PathVariable(name = "componentFragment") String componentFragment, @RequestBody FailureMode failureMode) {
        log.info("> addFailureMode - {}, {}", componentFragment, failureMode);
        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_Component, componentFragment);

        return repositoryService.addFailureMode(componentUri, failureMode);
    }

}
