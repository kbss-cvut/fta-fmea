package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.Function;
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
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/functions")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FunctionController {

    private final FunctionRepositoryService functionRepositoryService;
    private final IdentifierService identifierService;

    @GetMapping
    public List<Function> findAll() {
        return functionRepositoryService.findAll();
    }

    @GetMapping(value = "/{functionFragment}/requiredFunctions", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public Set<Function> getFunctions(@PathVariable(name = "functionFragment") String function) {
        log.info("> getRequiredFunctions - {}", function);
        URI functionUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, function);
        return functionRepositoryService.getFunctions(functionUri);
    }

    @PostMapping(value = "/{functionFragment}/requiredFunctions/{requiredFunctionFragment}")
    public Function addRequiredFunction(@PathVariable(name = "functionFragment") String functionFragment,@PathVariable(name = "requiredFunctionFragment") String requiredFunctionFragment ) {
        log.info("> addRequiredFunction - {}, {}", functionFragment, requiredFunctionFragment);
        URI functionUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, functionFragment);
        URI requiredFunctionURI = identifierService.composeIdentifier(Vocabulary.s_c_Function, requiredFunctionFragment);
        return functionRepositoryService.addRequiredFunction(functionUri, requiredFunctionURI);
    }

    @DeleteMapping(value = "/{functionFragment}/requiredFunctions/{requiredFunctionFragment}")
    public void deleteFunction(@PathVariable(name = "functionFragment") String functionFragment, @PathVariable(name = "requiredFunctionFragment") String requiredFunctionFragment) {
        log.info("> deleteRequiredFunction - {}, {}", functionFragment, functionFragment);
        URI functionUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, functionFragment);
        URI requiredFunctionUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, requiredFunctionFragment);

        functionRepositoryService.deleteFunction(functionUri,requiredFunctionUri);
        log.info("< deleteRequiredFunction");
    }

}

