package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.*;
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

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE}, produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FailureMode update(@RequestBody FailureMode failureMode) {
        log.info("> update - {}", failureMode);

        FailureMode updatedFailureMode = repositoryService.update(failureMode);

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
                                        ,@PathVariable(name = "impairedBehaviorFragment") String impairedBehaviorFragment ) {
        log.info("> addImpairedBehavior - {}, {}", failureModeFragment, impairedBehaviorFragment);
        URI failureModeUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, failureModeFragment);
        URI impairedBehaviorUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, impairedBehaviorFragment);
        return repositoryService.addImpairedBehavior(failureModeUri, impairedBehaviorUri);
    }

    @DeleteMapping(value = "/{failureModeFragment}/impairedBehavior/{impairedBehaviorFragment}")
    public void removeImpairedBehavior(@PathVariable(name = "failureModeFragment") String failureModeFragment
            ,@PathVariable(name = "impairedBehaviorFragment") String impairedBehaviorFragment ) {
        log.info("> removeImpairedBehavior - {}, {}", failureModeFragment, impairedBehaviorFragment);
        URI failureModeUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, failureModeFragment);
        URI impairedBehaviorUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, impairedBehaviorFragment);
        repositoryService.removeImpairedBehavior(failureModeUri, impairedBehaviorUri);
    }

}
