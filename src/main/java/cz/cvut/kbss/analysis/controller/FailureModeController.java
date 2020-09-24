package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.service.FailureModeRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController()
@RequestMapping("/failureMode")
public class FailureModeController {

    @Autowired
    private FailureModeRepositoryService failureModeRepositoryService;

    @GetMapping("/generateAndGet")
    public List<FailureMode> generateAndGet() {
        int randomNo = new Random().nextInt(100);
        FailureMode failureMode = new FailureMode();
        failureMode.setName("Failure Mode Number - " + randomNo);

        failureModeRepositoryService.persist(failureMode);

        return failureModeRepositoryService.findAll();
    }
}
