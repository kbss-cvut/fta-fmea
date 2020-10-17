package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.service.FailureModeRepositoryService;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/failureModes")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FailureModeController {

    private final FailureModeRepositoryService failureModeRepositoryService;

    @GetMapping(produces = JsonLd.MEDIA_TYPE)
    public List<FailureMode> findAll() {
        return failureModeRepositoryService.findAll();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> createFailureMode(@RequestBody FailureMode failureMode) {
        URI uri = failureModeRepositoryService.persist(failureMode);
        return ResponseEntity.created(uri).build();
    }

}
