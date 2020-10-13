package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.service.FailureModeRepositoryService;
import cz.cvut.kbss.jsonld.JsonLd;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/failureModes")
public class FailureModeController {

    @Autowired
    private FailureModeRepositoryService failureModeRepositoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public URI createFailureMode(@RequestBody FailureMode failureMode) {
        return failureModeRepositoryService.persist(failureMode);
    }

    // TODO dummy testing method
    @GetMapping("/createDummy")
    public List<FailureMode> generateAndGet() {
        int randomNo = new Random().nextInt(100);
        FailureMode failureMode = new FailureMode();
        failureMode.setName("Failure Mode Number - " + randomNo);

        failureModeRepositoryService.persist(failureMode);

        return failureModeRepositoryService.findAll();
    }

}
