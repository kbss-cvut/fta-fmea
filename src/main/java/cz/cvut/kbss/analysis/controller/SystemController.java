package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.model.System;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.service.SystemRepositoryService;
import cz.cvut.kbss.analysis.service.external.AnnotatorService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/systems")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SystemController {

    private final SystemRepositoryService repositoryService;
    private final IdentifierService identifierService;
    private final AnnotatorService annotatorService;

    @GetMapping
    public List<System> findAll() {
        return repositoryService.findAll();
    }

    @GetMapping(value = "/{systemFragment}", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public System find(@PathVariable(name = "systemFragment") String systemFragment) {
        log.info("> find - {}", systemFragment);
        URI systemUri = identifierService.composeIdentifier(Vocabulary.s_c_System, systemFragment);
        return repositoryService.findRequired(systemUri);
    }

    @GetMapping(value = "/{systemFragment}/failureModes", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public Set<FailureMode> getFailureModes(@PathVariable(name = "systemFragment") String systemFragment) {
        log.info("> getAllFailureModes - {}", systemFragment);
        URI systemUri = identifierService.composeIdentifier(Vocabulary.s_c_System, systemFragment);
        return repositoryService.getAllFailureModes(systemUri);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public System create(@RequestBody System system) {
        log.info("> create - {}", system);
        repositoryService.persist(system);
        return system;
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public System rename(@RequestBody System system) {
        log.info("> rename - {}", system);

        System updatedTree = repositoryService.rename(system);

        log.info("< rename - {}", updatedTree);
        return updatedTree;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{systemFragment}")
    public void delete(@PathVariable(name = "systemFragment") String systemFragment) {
        log.info("> delete - {}", systemFragment);

        URI systemUri = identifierService.composeIdentifier(Vocabulary.s_c_System, systemFragment);
        repositoryService.remove(systemUri);
    }

    @PostMapping(value = "/{systemFragment}/components/{componentFragment}")
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


    @PostMapping(value = "/{systemFragment}/documents")
    public void importDocument(@PathVariable(name = "systemFragment") String systemFragment, @RequestParam URI documentId) throws UnsupportedEncodingException {
        URI systemURI = identifierService.composeIdentifier(Vocabulary.s_c_System, systemFragment);
        log.info("> importing annotations from document <{}> into system <{}>", documentId, systemURI);
        annotatorService.processAnnotations();
        annotatorService.convertDocument(documentId.toString());
        repositoryService.importDocument(systemURI, documentId);
    }

}

