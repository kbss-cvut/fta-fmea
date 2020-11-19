package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.TreeNode;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.service.TreeNodeRepositoryService;
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
@RequestMapping("/treeNodes")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class TreeNodeController {

    private final IdentifierService identifierService;
    private final TreeNodeRepositoryService repositoryService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{treeNodeFragment}")
    public void delete(@PathVariable(name = "treeNodeFragment") String treeNodeFragment) {
        log.info("> delete - {}", treeNodeFragment);

        URI nodeUri = identifierService.composeIdentifier(Vocabulary.s_c_TreeNode, treeNodeFragment);
        repositoryService.delete(nodeUri);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE}, produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public void update(@RequestBody TreeNode node) {
        log.info("> update - updating node - {}", node);
        repositoryService.updateNode(node);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{treeNodeFragment}/inputEvents", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public TreeNode addInputEvent(@PathVariable(name = "treeNodeFragment") String treeNodeFragment, @RequestBody FaultEvent inputEvent) {
        log.info("> addInputEvent - {}, {}", treeNodeFragment, inputEvent);
        URI nodeUri = identifierService.composeIdentifier(Vocabulary.s_c_TreeNode, treeNodeFragment);

        return repositoryService.addInputEvent(nodeUri, inputEvent);
    }

    @GetMapping(value = "/{treeNodeFragment}/eventPathToRoot", produces = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public List<FaultEvent> eventPathToRoot(@PathVariable(name = "treeNodeFragment") String treeNodeFragment) {
        log.info("> eventPathToRoot - {}", treeNodeFragment);
        URI nodeUri = identifierService.composeIdentifier(Vocabulary.s_c_TreeNode, treeNodeFragment);

        return repositoryService.eventPathToRoot(nodeUri);
    }

}
