package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.TreeNode;
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
import java.util.Set;

@RestController
@RequestMapping("/faultEvents")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FaultEventController {

    private final FaultEventRepositoryService repositoryService;

    @GetMapping(produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public List<FaultEvent> findAll() {
        return repositoryService.findFaultEvents();
    }

}
