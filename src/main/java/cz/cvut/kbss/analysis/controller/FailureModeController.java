package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.service.FailureModeRepositoryService;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/failureModes")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FailureModeController {

    private final IdentifierService identifierService;
    private final FailureModeRepositoryService repositoryService;

    @GetMapping(value = "/{failureModeFragment}", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public FailureMode findFailureMode(@PathVariable(name = "failureModeFragment") String failureModeFragment) {
        URI failureModeUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, failureModeFragment);
        return repositoryService.find(failureModeUri);
    }

    @GetMapping(value = "/{failureModeFragment}/mitigation", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public Set<Mitigation> getMitigation(@PathVariable(name = "failureModeFragment") String failureModeFragment) {
        URI failureModeUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, failureModeFragment);
        return repositoryService.getMitigation(failureModeUri);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{failureModeFragment}/mitigation", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public Mitigation addMitigation(@PathVariable(name = "failureModeFragment") String failureModeFragment, @RequestBody Mitigation mitigation) {
        URI failureModeUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, failureModeFragment);

        return repositoryService.addMitigation(failureModeUri, mitigation);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{failureModeFragment}/manifestingEvent", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public TreeNode setManifestingEvent(@PathVariable(name = "failureModeFragment") String failureModeFragment, @RequestBody FaultEvent manifestingEvent) {
        URI failureModeUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureMode, failureModeFragment);

        return repositoryService.setManifestingEvent(failureModeUri, manifestingEvent);
    }

}
