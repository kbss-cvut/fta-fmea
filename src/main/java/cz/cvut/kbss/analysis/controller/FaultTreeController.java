package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.controller.event.PaginatedResultRetrievedEvent;
import cz.cvut.kbss.analysis.controller.util.FaultTreeFilterMapper;
import cz.cvut.kbss.analysis.controller.util.PagingUtils;
import cz.cvut.kbss.analysis.dao.util.FaultTreeFilterParams;
import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.model.opdata.OperationalDataFilter;
import cz.cvut.kbss.analysis.security.SecurityConstants;
import cz.cvut.kbss.analysis.service.FaultTreeEvaluationService;
import cz.cvut.kbss.analysis.service.FaultTreeRepositoryService;
import cz.cvut.kbss.analysis.service.FaultTreeService;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jsonld.JsonLd;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/faultTrees")
@PreAuthorize("hasRole('" + SecurityConstants.ROLE_USER + "')")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FaultTreeController {

    private final FaultTreeRepositoryService repositoryService;
    private final FaultTreeEvaluationService faultTreeEvaluationService;
    private final IdentifierService identifierService;
    private final FaultTreeService faultTreeService;
    private final ApplicationEventPublisher eventPublisher;

    @GetMapping
    public List<FaultTree> findAll() {
        return repositoryService.findAll();
    }

    @GetMapping("/summaries")
    public List<FaultTree> summaries(@RequestParam(required = false) MultiValueMap<String, String> params,
                                     UriComponentsBuilder uriBuilder, HttpServletResponse response) {
        FaultTreeFilterParams filterParams = FaultTreeFilterMapper.constructFaultTreeFilter(params);
        Page<FaultTree> result = repositoryService.findAllSummaries(filterParams, PagingUtils.resolvePaging(params));
        eventPublisher.publishEvent(new PaginatedResultRetrievedEvent(this, uriBuilder, response, result));
        return result.getContent();
    }

    @GetMapping(value = "/{faultTreeFragment}", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public FaultTree find(@PathVariable(name = "faultTreeFragment") String faultTreeFragment) {
        log.info("> find - {}", faultTreeFragment);
        URI faultTreeUri = identifierService.composeIdentifier(Vocabulary.s_c_fault_tree, faultTreeFragment);
        return faultTreeService.findWithDetails(faultTreeUri);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FaultTree create(@RequestBody FaultTree faultTree) {
        log.info("> create - {}", faultTree);
        repositoryService.createTree(faultTree);
        return faultTree;
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

        URI faultTreeUri = identifierService.composeIdentifier(Vocabulary.s_c_fault_tree, faultTreeFragment);
        repositoryService.remove(faultTreeUri);
    }

    @GetMapping(value = "/{faultTreeFragment}/reusableEvents", produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public List<FaultEvent> reusableEvents(@PathVariable(name = "faultTreeFragment") String faultTreeFragment) {
        log.info("> reusableEvents - {}", faultTreeFragment);
        URI faultTreeUri = identifierService.composeIdentifier(Vocabulary.s_c_fault_tree, faultTreeFragment);

        return repositoryService.getReusableEvents(faultTreeUri);
    }

    @GetMapping(value = "/{faultTreeFragment}/treePaths", produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public List<List<FaultEvent>> getTreePaths(@PathVariable(name = "faultTreeFragment") String faultTreeFragment) {
        log.info("> getTreePaths - {}", faultTreeFragment);
        URI faultTreeUri = identifierService.composeIdentifier(Vocabulary.s_c_fault_tree, faultTreeFragment);

        return repositoryService.getTreePaths(faultTreeUri);
    }

    @GetMapping(value = "/treeAggregate/treePathsAggregate", produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public List<List<FaultEvent>> getAggregateTreePaths() {
        log.info("> getAggregateTreePaths");

        return repositoryService.getTreePathsAggregate();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{faultTreeFragment}/failureModesTable", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FailureModesTable createFailureModesTable(
            @PathVariable(name = "faultTreeFragment") String faultTreeFragment,
            @RequestBody FailureModesTable failureModesTable) {
        log.info("> createFailureModesTable - {}, {}", faultTreeFragment, failureModesTable);
        URI faultTreeUri = identifierService.composeIdentifier(Vocabulary.s_c_fault_tree, faultTreeFragment);

        return repositoryService.createFailureModesTable(faultTreeUri, failureModesTable);
    }

    @GetMapping(value = "/{faultTreeFragment}/failureModesTable", produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FailureModesTable getFailureModesTable(@PathVariable(name = "faultTreeFragment") String faultTreeFragment) {
        log.info("> getFailureModesTable - {}", faultTreeFragment);
        URI faultTreeUri = identifierService.composeIdentifier(Vocabulary.s_c_fault_tree, faultTreeFragment);

        return repositoryService.getFailureModesTable(faultTreeUri);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{functionFragment}/generateFunctionalDependencies/{faultTreeName}", produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FaultTree generateFunctionalDependenciesFaultTree(@PathVariable("functionFragment") String functionFragment,@PathVariable("faultTreeName") String faultTreeName) throws URISyntaxException {
        URI functionUri = identifierService.composeIdentifier(Vocabulary.s_c_function, functionFragment);
        log.info("> generateFunctionalDependenciesFaultTree - {}, {}", functionFragment, faultTreeName);
        return repositoryService.generateFunctionDependencyTree(functionUri,faultTreeName);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/{faultTreeFragment}/cutsets")
    public void performCutSetAnalysis(@PathVariable(name = "faultTreeFragment") String faultTreeFragment){
        URI faultTreeUri = identifierService.composeIdentifier(Vocabulary.s_c_fault_tree, faultTreeFragment);
        log.info("> performCutSetAnalysis - {}", faultTreeFragment);
        repositoryService.performCutSetAnalysis(faultTreeUri);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/{faultTreeFragment}/evaluate")
    public void evaluate(@PathVariable(name = "faultTreeFragment") String faultTreeFragment, @RequestBody OperationalDataFilter filter){
        URI faultTreeUri = identifierService.composeIdentifier(Vocabulary.s_c_fault_tree, faultTreeFragment);
        log.info("> evaluate - {}", faultTreeFragment);
        faultTreeEvaluationService.evaluate(faultTreeUri, filter);
    }

}
