package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.model.Function;
import cz.cvut.kbss.analysis.service.FunctionRepositoryService;
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
@RequestMapping("/functions")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FunctionController {

    private final IdentifierService identifierService;
    private final FunctionRepositoryService repositoryService;

    @GetMapping(value = "/{functionFragment}/failureModes", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public Set<FailureMode> getFailureModes(@PathVariable(name = "functionFragment") String functionFragment) {
        URI functionUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, functionFragment);
        return repositoryService.getFailureModes(functionUri);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{functionFragment}/failureModes", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public ResponseEntity<Void> addFailureMode(@PathVariable(name = "functionFragment") String functionFragment, @RequestBody FailureMode failureMode) {
        URI functionUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, functionFragment);

        URI failureModeUri = repositoryService.addFailureMode(functionUri, failureMode);
        return ResponseEntity.created(failureModeUri).build();
    }

}
