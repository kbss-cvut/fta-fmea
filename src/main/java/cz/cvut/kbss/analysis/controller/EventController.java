package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.service.EventRepositoryService;
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
@RequestMapping("/events")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventController {

    private final IdentifierService identifierService;
    private final EventRepositoryService repositoryService;

    @GetMapping(value = "/{eventFragment}/inputEvents", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public Set<FaultEvent> getInputEvents(@PathVariable(name = "eventFragment") String eventFragment) {
        URI gateUri = identifierService.composeIdentifier(Vocabulary.s_c_Gate, eventFragment);
        return repositoryService.getInputEvents(gateUri);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{eventFragment}/inputEvents", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public ResponseEntity<Void> addInputEvent(@PathVariable(name = "eventFragment") String eventFragment, @RequestBody FaultEvent inputEvent) {
        URI gateUri = identifierService.composeIdentifier(Vocabulary.s_c_Gate, eventFragment);
        URI faultEventUri = identifierService.composeIdentifier(Vocabulary.s_c_FaultEvent, eventFragment);

        URI inputEventUri = repositoryService.addInputEvent(gateUri, faultEventUri, inputEvent);
        return ResponseEntity.created(inputEventUri).build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{eventFragment}/gate", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public ResponseEntity<Void> insertGate(@PathVariable(name = "eventFragment") String eventFragment, @RequestBody Gate gate) {
        URI faultEventUri = identifierService.composeIdentifier(Vocabulary.s_c_FaultEvent, eventFragment);

        URI gateUri = repositoryService.insertGate(faultEventUri, gate);
        return ResponseEntity.created(gateUri).build();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{eventFragment}/takenAction", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public ResponseEntity<Void> setTakenAction(@PathVariable(name = "eventFragment") String eventFragment, @RequestBody TakenAction takenAction) {
        URI eventUri = identifierService.composeIdentifier(Vocabulary.s_c_FaultEvent, eventFragment);

        URI takenActionUri = repositoryService.setTakenAction(eventUri, takenAction);
        return ResponseEntity.created(takenActionUri).build();
    }

}
