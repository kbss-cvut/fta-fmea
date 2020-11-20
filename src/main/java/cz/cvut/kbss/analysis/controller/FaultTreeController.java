package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.FaultTree;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.service.FaultTreeRepositoryService;
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

@RestController
@RequestMapping("/faultTrees")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FaultTreeController {

    private final FaultTreeRepositoryService repositoryService;
    private final IdentifierService identifierService;

    @GetMapping
    public List<FaultTree> findAll(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return repositoryService.findAllForUser(user);
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

        URI nodeUri = identifierService.composeIdentifier(Vocabulary.s_c_FaultTree, faultTreeFragment);
        repositoryService.delete(nodeUri);
    }

    @GetMapping(value = "/{faultTreeFragment}/rootToLeafEventPath/{leafEventFragment}", produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public List<FaultEvent> rootToLeafEventPath(
            @PathVariable(name = "faultTreeFragment") String faultTreeFragment,
            @PathVariable(name = "leafEventFragment") String leafEventFragment
    ) {
        log.info("> rootToLeafEventPath - {}, {}", faultTreeFragment, leafEventFragment);
        URI treeUri = identifierService.composeIdentifier(Vocabulary.s_c_FaultTree, faultTreeFragment);
        URI leafEventUri = identifierService.composeIdentifier(Vocabulary.s_c_FaultEvent, leafEventFragment);

        return repositoryService.rootToLeafEventPath(treeUri, leafEventUri);
    }

}
