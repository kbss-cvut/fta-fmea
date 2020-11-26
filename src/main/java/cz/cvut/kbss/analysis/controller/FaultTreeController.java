package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.service.FaultTreeRepositoryService;
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
@RequestMapping("/faultTrees")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FaultTreeController {

    private final FaultTreeRepositoryService repositoryService;
    private final IdentifierService identifierService;

    @GetMapping
    public List<FaultTree> findAll() {
        return repositoryService.findAll();
    }

    @GetMapping(value = "/{faultTreeFragment}", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public FaultTree find(@PathVariable(name = "faultTreeFragment") String faultTreeFragment) {
        log.info("> find - {}", faultTreeFragment);
        URI faultTreeUri = identifierService.composeIdentifier(Vocabulary.s_c_FaultTree, faultTreeFragment);
        return repositoryService.find(faultTreeUri);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FaultTree create(@RequestBody FaultTree faultTree) {
        log.info("> create - {}", faultTree);
        return repositoryService.create(faultTree);
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FaultTree update(@RequestBody FaultTree faultTree) {
        log.info("> update - {}", faultTree);

        FaultTree updatedTree = repositoryService.update(faultTree);

        log.info("< update - {}", updatedTree);
        return updatedTree;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{faultTreeFragment}")
    public void delete(@PathVariable(name = "faultTreeFragment") String faultTreeFragment) {
        log.info("> delete - {}", faultTreeFragment);

        URI faultTreeUri = identifierService.composeIdentifier(Vocabulary.s_c_FaultTree, faultTreeFragment);
        repositoryService.delete(faultTreeUri);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{faultTreeFragment}/failureModesTable", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FailureModesTable createFailureModesTable(
            @PathVariable(name = "faultTreeFragment") String faultTreeFragment,
            @RequestBody FailureModesTable failureModesTable) {
        log.info("> createFailureModesTable - {}, {}", faultTreeFragment, failureModesTable);
        URI faultTreeUri = identifierService.composeIdentifier(Vocabulary.s_c_FaultTree, faultTreeFragment);

        return repositoryService.createFailureModesTable(faultTreeUri, failureModesTable);
    }

}
