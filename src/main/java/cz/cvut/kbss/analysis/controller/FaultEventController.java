package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.service.FaultEventRepositoryService;
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

@RestController
@RequestMapping("/faultEvents")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FaultEventController {
    
    private final FaultEventRepositoryService repositoryService;
    private final IdentifierService identifierService;

    @GetMapping(produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public List<FaultEvent> findAll() {
        return repositoryService.findFaultEvents();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{faultEventFragment}")
    public void delete(@PathVariable(name = "faultEventFragment") String faultEventFragment) {
        log.info("> delete - {}", faultEventFragment);

        URI nodeUri = identifierService.composeIdentifier(Vocabulary.s_c_FaultEvent, faultEventFragment);
        repositoryService.delete(nodeUri);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE}, produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public void update(@RequestBody FaultEvent faultEvent) {
        log.info("> update - updating event - {}", faultEvent);
        repositoryService.updateEvent(faultEvent);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{faultEventFragment}/inputEvents", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FaultEvent addInputEvent(@PathVariable(name = "faultEventFragment") String faultEventFragment, @RequestBody FaultEvent inputEvent) {
        log.info("> addInputEvent - {}, {}", faultEventFragment, inputEvent);
        URI faultEventUri = identifierService.composeIdentifier(Vocabulary.s_c_FaultEvent, faultEventFragment);

        return repositoryService.addInputEvent(faultEventUri, inputEvent);
    }

    @GetMapping(value = "/{faultEventFragment}/eventPathToRoot", produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public List<FaultEvent> eventPathToRoot(@PathVariable(name = "faultEventFragment") String faultEventFragment) {
        log.info("> eventPathToRoot - {}", faultEventFragment);
        URI nodeUri = identifierService.composeIdentifier(Vocabulary.s_c_FaultEvent, faultEventFragment);

        return repositoryService.eventPathToRoot(nodeUri);
    }
    
}
