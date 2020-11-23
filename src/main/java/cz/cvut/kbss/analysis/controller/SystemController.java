package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.System;
import cz.cvut.kbss.analysis.service.SystemRepositoryService;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/systems")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SystemController {

    private final SystemRepositoryService repositoryService;
    private final IdentifierService identifierService;

    @GetMapping
    public List<System> findAll() {
        return repositoryService.findAll();
    }

    @GetMapping(value = "/{systemFragment}", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public System find(@PathVariable(name = "systemFragment") String systemFragment) {
        log.info("> find - {}", systemFragment);
        URI systemUri = identifierService.composeIdentifier(Vocabulary.s_c_System, systemFragment);
        return repositoryService.find(systemUri);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public System create(@RequestBody System system) {
        log.info("> create - {}", system);
        return repositoryService.create(system);
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public System update(@RequestBody System system) {
        log.info("> update - {}", system);

        System updatedTree = repositoryService.update(system);

        log.info("< update - {}", updatedTree);
        return updatedTree;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{systemFragment}")
    public void delete(@PathVariable(name = "systemFragment") String systemFragment) {
        log.info("> delete - {}", systemFragment);

        URI systemUri = identifierService.composeIdentifier(Vocabulary.s_c_System, systemFragment);
        repositoryService.delete(systemUri);
    }

    @PutMapping(value = "/{systemFragment}/components/{componentFragment}")
    public void addComponent(@PathVariable(name = "systemFragment") String systemFragment,
                             @PathVariable(name = "componentFragment") String componentFragment) {
        log.info("> addComponent - {}, {}", systemFragment, componentFragment);
        URI systemUri = identifierService.composeIdentifier(Vocabulary.s_c_System, systemFragment);
        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_Component, componentFragment);

        repositoryService.addComponent(systemUri, componentUri);
    }

    @DeleteMapping(value = "/{systemFragment}/components/{componentFragment}")
    public void deleteComponent(@PathVariable(name = "systemFragment") String systemFragment,
                                @PathVariable(name = "componentFragment") String componentFragment) {
        log.info("> deleteComponent - {}, {}", systemFragment, componentFragment);
        URI systemUri = identifierService.composeIdentifier(Vocabulary.s_c_System, systemFragment);
        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_Component, componentFragment);

        repositoryService.removeComponent(systemUri, componentUri);
    }

}

