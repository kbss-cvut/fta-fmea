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
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Set;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventController {

    private final IdentifierService identifierService;
    private final EventRepositoryService repositoryService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE}, produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public void update(@RequestBody TreeNode node) {
        repositoryService.updateNode(node);
    }

    @GetMapping(value = "/{treeNodeFragment}/inputEvents", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public Set<Event> getInputEvents(@PathVariable(name = "treeNodeFragment") String treeNodeFragment) {
        URI gateUri = identifierService.composeIdentifier(Vocabulary.s_c_TreeNode, treeNodeFragment);
        return repositoryService.getInputEvents(gateUri);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{treeNodeFragment}/inputEvents", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public TreeNode addInputEvent(@PathVariable(name = "treeNodeFragment") String treeNodeFragment, @RequestBody FaultEvent inputEvent) {
        URI nodeUri = identifierService.composeIdentifier(Vocabulary.s_c_TreeNode, treeNodeFragment);

        return repositoryService.addInputEvent(nodeUri, inputEvent);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{treeNodeFragment}/gate", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public TreeNode insertGate(@PathVariable(name = "treeNodeFragment") String treeNodeFragment, @RequestBody Gate gate) {
        URI nodeUri = identifierService.composeIdentifier(Vocabulary.s_c_TreeNode, treeNodeFragment);

        return repositoryService.insertGate(nodeUri, gate);
    }

}
