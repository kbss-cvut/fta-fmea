package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.Behavior;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.Function;
import cz.cvut.kbss.analysis.service.FunctionRepositoryService;
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
    public Set<Behavior> getFunctions(@PathVariable(name = "functionFragment") String function) {
        log.info("> getRequiredFunctions - {}", function);
        URI functionUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, function);
        return functionRepositoryService.getRequiredBehavior(functionUri);
    }

    @PostMapping(value = "/{functionFragment}/requiredFunctions/{requiredFunctionFragment}")
    public Function addRequiredFunction(@PathVariable(name = "functionFragment") String functionFragment,@PathVariable(name = "requiredFunctionFragment") String requiredFunctionFragment ) {
        log.info("> addRequiredFunction - {}, {}", functionFragment, requiredFunctionFragment);
        URI functionUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, functionFragment);
        URI requiredFunctionURI = identifierService.composeIdentifier(Vocabulary.s_c_Function, requiredFunctionFragment);
        return functionRepositoryService.addRequiredBehavior(functionUri, requiredFunctionURI);
    }

    @DeleteMapping(value = "/{functionFragment}/requiredFunctions/{requiredFunctionFragment}")
    public void deleteFunction(@PathVariable(name = "functionFragment") String functionFragment, @PathVariable(name = "requiredFunctionFragment") String requiredFunctionFragment) {
        log.info("> deleteRequiredFunction - {}, {}", functionFragment, functionFragment);
        URI functionUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, functionFragment);
        URI requiredFunctionUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, requiredFunctionFragment);

        functionRepositoryService.deleteRequiredBehavior(functionUri,requiredFunctionUri);
        log.info("< deleteRequiredFunction");
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE},produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public void updateFunction(@RequestBody Function function){
        log.info("> update - {}", function);
        functionRepositoryService.update(function);
    }

    @GetMapping(value = "/{functionFragment}/getComponent", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public Component getComponent(@PathVariable(name = "functionFragment") String function) {
        log.info("> getComponent - {}", function);
        URI functionUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, function);
        return functionRepositoryService.getComponent(functionUri);
    }

    @GetMapping(value = "/{functionFragment}/impairedBehaviors", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public List<Behavior> getImpairedBehaviors(@PathVariable(name = "functionFragment") String function) {
        log.info("> getImpairedBehaviors - {}", function);
        URI functionUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, function);
        return functionRepositoryService.getImpairingBehaviors(functionUri);
    }

    @PostMapping(value = "/{functionFragment}/childBehavior/{childBehaviorFragment}")
    public void addChildBehavior(@PathVariable(name = "functionFragment") String functionFragment
            ,@PathVariable(name = "childBehaviorFragment") String childBehaviorFragment ) {
        log.info("> addChildBehavior - {}, {}", functionFragment, childBehaviorFragment);
        URI functionUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, functionFragment);
        URI childBehaviorUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, childBehaviorFragment);
        functionRepositoryService.addChildBehavior(functionUri, childBehaviorUri);
    }

    @DeleteMapping(value = "/{functionFragment}/childBehavior/{childBehaviorFragment}")
    public void removeChildBehavior(@PathVariable(name = "functionFragment") String failureModeFragment
            ,@PathVariable(name = "childBehaviorFragment") String childBehaviorFragment ) {
        log.info("> removeChildBehavior - {}, {}", failureModeFragment, childBehaviorFragment);
        URI functionUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, failureModeFragment);
        URI childBehaviorUri = identifierService.composeIdentifier(Vocabulary.s_c_Function, childBehaviorFragment);
        functionRepositoryService.removeChildBehavior(functionUri, childBehaviorUri);
    }

}

