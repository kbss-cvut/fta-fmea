package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.dto.URIReference;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.service.FunctionRepositoryService;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/functions")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FunctionController {

    private final IdentifierService identifierService;
    private final FunctionRepositoryService repositoryService;

    @GetMapping(value = "/{functionFragment}/failureModes", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public Set<FailureMode> getFailureModes(@PathVariable(name = "functionFragment") String functionFragment) {
        log.info("> getFailureModes - {}", functionFragment);
        URI functionUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, functionFragment);
        return repositoryService.getFailureModes(functionUri);
    }

    @PostMapping(value = "/{functionFragment}/failureModes", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FailureMode addFailureMode(@PathVariable(name = "functionFragment") String functionFragment, @RequestBody URIReference failureModeReference) {
        log.info("> addFailureMode - {}, {}", functionFragment, failureModeReference);
        URI functionUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, functionFragment);

        return repositoryService.addFailureMode(functionUri, failureModeReference);
    }

}
