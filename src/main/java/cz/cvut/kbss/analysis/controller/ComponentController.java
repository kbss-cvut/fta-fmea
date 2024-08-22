package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.dto.update.ComponentUpdateDTO;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.model.Function;
import cz.cvut.kbss.analysis.security.SecurityConstants;
import cz.cvut.kbss.analysis.service.ComponentRepositoryService;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jsonld.JsonLd;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/components")
@PreAuthorize("hasRole('" + SecurityConstants.ROLE_USER + "')")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ComponentController {

    private final ComponentRepositoryService repositoryService;
    private final IdentifierService identifierService;

    @Operation(summary = "Retrieve all components")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all components")
    })
    @GetMapping(produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public List<Component> findAll() {
        return repositoryService.findAll();
    }


    @Operation(summary = "Create a new component")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Component created successfully"),
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public Component create(@RequestBody Component component) {
        log.info("> create - {}", component);
        repositoryService.persist(component);
        return component;
    }

    @Operation(summary = "Update an existing component")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Component updated successfully"),
    })
    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE}, produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public Component update(@RequestBody ComponentUpdateDTO componentUpdate) {
        log.info("> update - {}", componentUpdate);
        return repositoryService.updateByDTO(componentUpdate);
    }

    @Operation(summary = "Delete a component")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Component deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Component not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{componentFragment}")
    public void delete(@PathVariable(name = "componentFragment") String componentFragment) {
        log.info("> delete - {}", componentFragment);

        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_component, componentFragment);
        repositoryService.remove(componentUri);
    }

    @Operation(summary = "Retrieve functions of a component")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Functions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Component not found")
    })
    @GetMapping(value = "/{componentFragment}/functions", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public Set<Function> getFunctions(@PathVariable(name = "componentFragment") String componentFragment) {
        log.info("> getFunctions - {}", componentFragment);
        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_component, componentFragment);
        return repositoryService.getFunctions(componentUri);
    }

    @Operation(summary = "Retrieve failure modes of a component")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Failure modes retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Component not found")
    })
    @GetMapping(value = "/{componentFragment}/failureModes", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public Set<FailureMode> getFailureModes(@PathVariable(name = "componentFragment") String componentFragment) {
        log.info("> getFailureModes - {}", componentFragment);
        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_component, componentFragment);
        return repositoryService.getFailureModes(componentUri);
    }


    @Operation(summary = "Add a failure mode to a component")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Failure mode added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Component not found")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{componentFragment}/failureModes", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FailureMode addFailureMode(@PathVariable(name = "componentFragment") String componentFragment, @RequestBody FailureMode failureMode) {
        log.info("> addFailureMode - {}, {}", componentFragment, failureMode);
        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_component, componentFragment);

        return repositoryService.addFailureMode(componentUri,failureMode);
    }

    @Operation(summary = "Add a failure mode by URI to a component")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Failure mode added by URI successfully"),
            @ApiResponse(responseCode = "404", description = "Component or failure mode not found")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{componentFragment}/failureModes/{failureModeFragment}", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public void addFailureModeByURI(@PathVariable(name = "componentFragment") String componentFragment, @PathVariable(name = "failureModeFragment") String failureModeFragment) {
        log.info("> addFailureModeByUri - {}, {}", componentFragment, failureModeFragment);
        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_component, componentFragment);
        URI failureModeUri = identifierService.composeIdentifier(Vocabulary.s_c_failure_mode, failureModeFragment);
        repositoryService.addFailureModeByUri(componentUri, failureModeUri);
    }

    @Operation(summary = "Delete a failure mode from a component")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Failure mode deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Component or failure mode not found")
    })
    @DeleteMapping(value = "/{componentFragment}/failureModes/{failureModeFragment}")
    public void deleteFailureMode(@PathVariable(name = "componentFragment") String componentFragment, @PathVariable(name = "failureModeFragment") String failureModeFragment) {
        log.info("> deleteFailureMode - {}, {}", componentFragment, failureModeFragment);
        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_component, componentFragment);
        URI failureModeUri = identifierService.composeIdentifier(Vocabulary.s_c_failure_mode, failureModeFragment);

        repositoryService.deleteFailureMode(componentUri, failureModeUri);
        log.info("< deleteFailureMode");
    }

    @Operation(summary = "Add a function to a component")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Function added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Component not found")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{componentFragment}/functions", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public Function addFunction(@PathVariable(name = "componentFragment") String componentFragment, @RequestBody Function function) {
        log.info("> addFunction - {}, {}", componentFragment, function);
        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_component, componentFragment);

        return repositoryService.addFunction(componentUri, function);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{componentFragment}/functions/{functionFragment}", produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public Function addFunctionByURI(@PathVariable(name = "componentFragment") String componentFragment, @PathVariable(name = "functionFragment") String functionFragment) {
        log.info("> addFunction - {}, {}", componentFragment, functionFragment);
        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_component, componentFragment);
        URI functionUri = identifierService.composeIdentifier(Vocabulary.s_c_function, functionFragment);
        
        return repositoryService.addFunctionByURI(componentUri, functionUri);
    }

    @Operation(summary = "Add a function by URI to a component")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Function added by URI successfully"),
            @ApiResponse(responseCode = "404", description = "Component or function not found")
    })
    @DeleteMapping(value = "/{componentFragment}/functions/{functionFragment}")
    public void deleteFunction(@PathVariable(name = "componentFragment") String componentFragment, @PathVariable(name = "functionFragment") String functionFragment) {
        log.info("> deleteFunction - {}, {}", componentFragment, functionFragment);
        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_component, componentFragment);
        URI functionUri = identifierService.composeIdentifier(Vocabulary.s_c_function, functionFragment);

        repositoryService.deleteFunction(componentUri, functionUri);
        log.info("< deleteFunction");
    }

    @Operation(summary = "Link two components")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Components linked successfully"),
            @ApiResponse(responseCode = "404", description = "Component not found")
    })
    @PostMapping(value = "/{componentFragment}/linkComponent/{linkFragment}")
    public Component linkComponents(
            @PathVariable(name = "componentFragment") String componentFragment,
            @PathVariable(name = "linkFragment") String linkFragment) {
        log.info("> addFunction - {}, {}", componentFragment, linkFragment);
        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_component, componentFragment);
        URI linkComponentUri = identifierService.composeIdentifier(Vocabulary.s_c_component, linkFragment);

        return repositoryService.linkComponents(componentUri, linkComponentUri);
    }

    @Operation(summary = "Unlink a component from its linked components")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Component unlinked successfully"),
            @ApiResponse(responseCode = "404", description = "Component not found")
    })
    @DeleteMapping(value = "/{componentFragment}/linkComponent")
    public void unlinkComponents(@PathVariable(name = "componentFragment") String componentFragment) {
        log.info("> unlinkComponents - {}", componentFragment);
        URI componentUri = identifierService.composeIdentifier(Vocabulary.s_c_component, componentFragment);

        repositoryService.unlinkComponents(componentUri);
        log.info("< unlinkComponents");
    }

    @Operation(summary = "Merge two components into one")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Components merged successfully"),
            @ApiResponse(responseCode = "404", description = "One or both components not found")
    })
    @PostMapping(value = "/mergeComponents/{sourceFragment}/{targetFragment}")
    public void mergeComponents(@PathVariable(name = "sourceFragment") String sourceFragment
            ,@PathVariable(name = "targetFragment") String targetFragment){
        log.info("> mergeComponents - {} {}", sourceFragment, targetFragment);

        URI sourceUri = identifierService.composeIdentifier(Vocabulary.s_c_component, sourceFragment);
        URI targetUri = identifierService.composeIdentifier(Vocabulary.s_c_component, targetFragment);

        repositoryService.mergeComponents(sourceUri, targetUri);
    }
}
