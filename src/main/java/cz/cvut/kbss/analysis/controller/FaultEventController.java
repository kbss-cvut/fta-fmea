package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.FaultEventType;
import cz.cvut.kbss.analysis.model.diagram.Rectangle;
import cz.cvut.kbss.analysis.security.SecurityConstants;
import cz.cvut.kbss.analysis.service.FaultEventRepositoryService;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/faultEvents")
@PreAuthorize("hasRole('" + SecurityConstants.ROLE_USER + "')")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FaultEventController {

    private final FaultEventRepositoryService repositoryService;
    private final IdentifierService identifierService;

    @GetMapping(produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public List<FaultEvent> findAll() {
        return repositoryService.findAll();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{faultEventFragment}")
    public void delete(@PathVariable(name = "faultEventFragment") String faultEventFragment) {
        log.info("> delete - {}", faultEventFragment);

        URI faultEventIri = identifierService.composeIdentifier(Vocabulary.s_c_fault_event, faultEventFragment);
        FaultEvent instance = repositoryService.findRequired(faultEventIri);
        repositoryService.remove(instance);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE}, produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public void update(@RequestBody FaultEvent faultEvent) {
        log.info("> update - updating event - {}", faultEvent);
        repositoryService.update(faultEvent);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/{faultEventFragment}/rectangle", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE}, produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public void updateRectangle(@PathVariable(name = "faultEventFragment") String faultEventFragment, @RequestBody Rectangle rectangle) {
        URI faultEventUri = identifierService.composeIdentifier(Vocabulary.s_c_fault_event, faultEventFragment);
        log.trace("> update - updating rectangle - {} for event <{}>", rectangle, faultEventUri);
        repositoryService.update(rectangle);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{faultEventFragment}/inputEvents", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FaultEvent addInputEvent(@PathVariable(name = "faultEventFragment") String faultEventFragment, @RequestBody FaultEvent inputEvent) {
        log.info("> addInputEvent - {}, {}", faultEventFragment, inputEvent);
        URI faultEventUri = identifierService.composeIdentifier(Vocabulary.s_c_fault_event, faultEventFragment);

        return repositoryService.addInputEvent(faultEventUri, inputEvent);
    }

    @GetMapping(value = "/{faultEventFragment}/failureMode", produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FailureMode getFailureMode(@PathVariable(name = "faultEventFragment") String faultEventFragment) {
        log.info("> getFailureMode - {}", faultEventFragment);
        URI faultEventUri = identifierService.composeIdentifier(Vocabulary.s_c_fault_event, faultEventFragment);

        return repositoryService.getFailureMode(faultEventUri);
    }

    @GetMapping(value = "/top-fault-events/{systemFragment}", produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public List<FaultEventType> getTopFaultEvents(@PathVariable String systemFragment){
        log.info("> getFaultEventTypes - {}", systemFragment);
        URI systemUri = identifierService.composeIdentifier(Vocabulary.s_c_system, systemFragment);
        return repositoryService.getTopFaultEvents(systemUri);
    }

    @GetMapping(value = "/all-fault-events/{faultTreeFragment}", produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public List<FaultEventType> getAllFaultEvents(@PathVariable String faultTreeFragment){
        log.info("> getFaultEventTypes - {}", faultTreeFragment);
        URI systemUri = identifierService.composeIdentifier(Vocabulary.s_c_fault_tree, faultTreeFragment);
        return repositoryService.getAllFaultEvents(systemUri);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{faultEventFragment}/failureMode", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FailureMode addFailureMode(@PathVariable(name = "faultEventFragment") String faultEventFragment, @RequestBody FailureMode failureMode) {
        log.info("> addFailureMode - {}, {}", faultEventFragment, failureMode);
        URI faultEventUri = identifierService.composeIdentifier(Vocabulary.s_c_fault_event, faultEventFragment);

        return repositoryService.addFailureMode(faultEventUri, failureMode);
    }

    @DeleteMapping(value = "/{faultEventFragment}/failureMode")
    public void deleteFailureMode(@PathVariable(name = "faultEventFragment") String faultEventFragment) {
        log.info("> deleteFailureMode - {}", faultEventFragment);
        URI faultEventUri = identifierService.composeIdentifier(Vocabulary.s_c_fault_event, faultEventFragment);

        repositoryService.deleteFailureMode(faultEventUri);
        log.info("< deleteFailureMode");
    }

    @PutMapping(value = "/{faultEventFragment}/childrenSequence")
    public void updateChildrenSequence(
            @PathVariable(name = "faultEventFragment") String faultEventFragment,
            @RequestBody List<URI> childrenSequence) {
        log.info("> updateChildrenSequence - {}, {}", faultEventFragment, childrenSequence);
        URI faultEventUri = identifierService.composeIdentifier(Vocabulary.s_c_fault_event, faultEventFragment);

        repositoryService.updateChildrenSequence(faultEventUri, childrenSequence);
    }

}
