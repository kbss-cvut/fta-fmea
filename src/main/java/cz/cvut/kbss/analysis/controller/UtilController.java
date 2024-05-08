package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.config.conf.RepositoryConf;
import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.model.util.Exporter;
import cz.cvut.kbss.analysis.service.FaultEventRepositoryService;
import cz.cvut.kbss.analysis.service.FaultTreeRepositoryService;
import cz.cvut.kbss.analysis.service.util.CreateExampleDomainModel1;
import cz.cvut.kbss.analysis.service.util.MigrateFaultTreeToContexts;
import cz.cvut.kbss.analysis.util.Vocabulary;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.lang.System;
import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/utils")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UtilController {

    private final FaultEventRepositoryService faultEventService;
    private final FaultTreeRepositoryService faultTreeService;

    private final CreateExampleDomainModel1 factory;

//    private final MigrateFaultTreeToContexts processor;


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/faultTrees")
    public void deleteAll(){
        // delete all fault trees
        List<FaultTree> faultTrees = faultTreeService.findAll();
        for(FaultTree ft : faultTrees ){
            faultTreeService.remove(ft.getUri());
        }
        // delete all fault events
        List<FaultEvent> faultEventList = faultEventService.findAll();
        for(FaultEvent fe : faultEventList ){
            faultEventService.remove(fe.getUri());
        }
    }

    @GetMapping(value = "/create-example-system")
    public List<? extends Item> createExampleSystem(){

        List<? extends Item> result = factory.createModel();
        String root = "c:\\Users\\kostobog\\Documents\\skola\\projects\\2022-ava\\code\\fta-fmea-model\\";
        File dir = new File(root, String.format("example-v0.3-snapshot-%d", System.currentTimeMillis()));
        if(!dir.exists()){
            dir.mkdirs();
        }
        factory.export(new File(dir, "sns-partonomy-example.trig").toString());
        factory.anonymize();
        factory.export(new File(dir, "sns-partonomy-example--anonymized.trig").toString());

        return result;
//        factory.export("");
//        n
//        return
//        Item sys1 = factory.acPartonomy1("acm1");
//        factory.persist(sys1);
//        Item sys2 = factory.acPartonomy1("acm2");
//        factory.persist(sys2);
//        return Stream.of(sys1).toList();
    }

    @GetMapping(value = "/fha-based-example")
    public List<FaultEventType> createFHABasedFailureRateEstimates(){
        List<FaultEventType> ret = factory.createFHABasedFailureRateEstimates();
        return ret;
    }

    @GetMapping(value = "/uri")
    public URI getURI(){
        return URI.create(Vocabulary.s_c_aircraft_model);
    }

//
//    @GetMapping("/migrate")
//    public void migrate(){
//        processor.move();
//    }

//    @GetMapping("/test-migration")
//    public void testMigration(){
//        processor.testMigration();
//    }
}
