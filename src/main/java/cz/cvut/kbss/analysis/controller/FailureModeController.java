package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.service.ComponentRepositoryService;
import cz.cvut.kbss.analysis.service.FailureModeRepositoryService;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/failureModes")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FailureModeController {

    private final IdentifierService identifierService;
    private final FailureModeRepositoryService repositoryService;

    @GetMapping
    public List<FailureMode> findAll() {
        return repositoryService.findAll();
    }

    @GetMapping(value = "/{failureModeFragment}", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public FailureMode findFailureMode(@PathVariable(name = "failureModeFragment") String failureModeFragment) {
        log.info("> findFailureMode - {}", failureModeFragment);
        URI failureModeUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, failureModeFragment);
        return repositoryService.findRequired(failureModeUri);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE}, produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FailureMode createFailureMode(@RequestBody FailureMode failureMode) {
        log.info("> createFailureMode - {} ", failureMode);
        return repositoryService.createFailureMode(failureMode);
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE}, produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FailureMode update(@RequestBody FailureMode failureMode) {
        log.info("> update - {}", failureMode);

        FailureMode updatedFailureMode = repositoryService.updateFailureModeProperties(failureMode);

        log.info("< update - {}", failureMode);
        return updatedFailureMode;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{failureModeFragment}")
    public void delete(@PathVariable(name = "failureModeFragment") String failureModeFragment) {
        log.info("> delete - {}", failureModeFragment);

        URI failureModeUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, failureModeFragment);
        repositoryService.remove(failureModeUri);
    }

    @PostMapping(value = "/{failureModeFragment}/impairedBehavior/{impairedBehaviorFragment}")
    public FailureMode addImpairedBehavior(@PathVariable(name = "failureModeFragment") String failureModeFragment
            , @PathVariable(name = "impairedBehaviorFragment") String impairedBehaviorFragment) {
        log.info("> addImpairedBehavior - {}, {}", failureModeFragment, impairedBehaviorFragment);
        URI failureModeUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, failureModeFragment);
        URI impairedBehaviorUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, impairedBehaviorFragment);
        return repositoryService.addImpairedBehavior(failureModeUri, impairedBehaviorUri);
    }

    @DeleteMapping(value = "/{failureModeFragment}/impairedBehavior/{impairedBehaviorFragment}")
    public void removeImpairedBehavior(@PathVariable(name = "failureModeFragment") String failureModeFragment
            , @PathVariable(name = "impairedBehaviorFragment") String impairedBehaviorFragment) {
        log.info("> removeImpairedBehavior - {}, {}", failureModeFragment, impairedBehaviorFragment);
        URI failureModeUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, failureModeFragment);
        URI impairedBehaviorUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, impairedBehaviorFragment);
        repositoryService.removeImpairedBehavior(failureModeUri, impairedBehaviorUri);
    }

    @PostMapping(value = "/{failureModeFragment}/requiredBehavior/{requiredBehaviorFragment}")
    public void addRequiredBehavior(@PathVariable(name = "failureModeFragment") String failureModeFragment
            , @PathVariable(name = "requiredBehaviorFragment") String requiredBehaviorFragment) {
        log.info("> addRequiredBehavior - {}, {}", failureModeFragment, requiredBehaviorFragment);
        URI failureModeUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, failureModeFragment);
        URI requiredBehaviorUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, requiredBehaviorFragment);
        repositoryService.addRequiredBehavior(failureModeUri, requiredBehaviorUri);
    }

    @DeleteMapping(value = "/{failureModeFragment}/requiredBehavior/{requiredBehaviorFragment}")
    public void removeRequiredBehavior(@PathVariable(name = "failureModeFragment") String failureModeFragment
            , @PathVariable(name = "requiredBehaviorFragment") String requiredBehaviorFragment) {
        log.info("> removeRequiredBehavior - {}, {}", failureModeFragment, requiredBehaviorFragment);
        URI failureModeUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, failureModeFragment);
        URI requiredBehaviorUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, requiredBehaviorFragment);
        repositoryService.removeRequiredBehavior(failureModeUri, requiredBehaviorUri);
    }

    @PostMapping(value = "/{failureModeFragment}/childBehavior/{childBehaviorFragment}")
    public void addChildBehavior(@PathVariable(name = "failureModeFragment") String failureModeFragment
            , @PathVariable(name = "childBehaviorFragment") String childBehaviorFragment) {
        log.info("> addChildBehavior - {}, {}", failureModeFragment, childBehaviorFragment);
        URI failureModeUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, failureModeFragment);
        URI childBehaviorUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, childBehaviorFragment);
        repositoryService.addChildBehavior(failureModeUri, childBehaviorUri);
    }

    @DeleteMapping(value = "/{failureModeFragment}/childBehavior/{childBehaviorFragment}")
    public void removeChildBehavior(@PathVariable(name = "failureModeFragment") String failureModeFragment
            , @PathVariable(name = "childBehaviorFragment") String childBehaviorFragment) {
        log.info("> removeChildBehavior - {}, {}", failureModeFragment, childBehaviorFragment);
        URI failureModeUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, failureModeFragment);
        URI childBehaviorUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, childBehaviorFragment);
        repositoryService.removeChildBehavior(failureModeUri, childBehaviorUri);
    }

    @GetMapping(value = "/{failureModeFragment}/childTransitiveClosure", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public Set<URI> getChildTransitiveClosure(@PathVariable(name = "failureModeFragment") String failureModeFragment) {
        log.info("> getChildTransitiveClosure - {}", failureModeFragment);
        URI failureModeUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, failureModeFragment);
        return repositoryService.getTransitiveClosure(failureModeUri, "child");
    }

    @GetMapping(value = "/{failureModeFragment}/requiredTransitiveClosure", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public Set<URI> getRequiredTransitiveClosure(@PathVariable(name = "failureModeFragment") String failureModeFragment) {
        log.info("> getRequiredTransitiveClosure - {}", failureModeFragment);
        URI failureModeUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, failureModeFragment);
        return repositoryService.getTransitiveClosure(failureModeUri, "required");
    }
}
