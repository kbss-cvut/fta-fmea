package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.service.TreeNodeRepositoryService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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

}
