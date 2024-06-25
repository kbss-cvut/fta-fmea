package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.opdata.OperationalDataFilter;
import cz.cvut.kbss.analysis.service.FaultTreeService;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.service.OperationalDataFilterService;
import cz.cvut.kbss.analysis.service.external.OperationalDataService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Controller
@RequestMapping("/operational-data-filter")
@Slf4j
public class OperationalDataFilterController {

    private final OperationalDataFilterService filterService;
    private final IdentifierService identifierService;
    private final OperationalDataService operationalDataService;
    private final FaultTreeService faultTreeService;

    public OperationalDataFilterController(OperationalDataFilterService filterService, IdentifierService identifierService, OperationalDataService operationalDataService, FaultTreeService faultTreeService) {
        this.filterService = filterService;
        this.identifierService = identifierService;
        this.operationalDataService = operationalDataService;
        this.faultTreeService = faultTreeService;
    }

    @PutMapping(path="reset", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public void reset(){
        filterService.removeFilter();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/system/{systemFragment}", consumes = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public void updateSystemFilter(@PathVariable(name = "systemFragment") String systemFragment, @RequestBody OperationalDataFilter filter){
        log.info("> updateSystemFilter - {} to {}", systemFragment, filter);
        URI systemUri = identifierService.composeIdentifier(Vocabulary.s_c_system, systemFragment);
        filterService.updateSystemFilter(systemUri, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(value = "/fault-tree/{faultTreeFragment}", consumes = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public void updateFaultTreeFilter(@PathVariable(name = "faultTreeFragment") String faultTreeFragment, @RequestBody OperationalDataFilter filter){
        log.info("> updateFaultTreeFilter - {} to {}", faultTreeFragment, filter);
        URI faultTreeUri = identifierService.composeIdentifier(Vocabulary.s_c_fault_tree, faultTreeFragment);
        faultTreeService.updateFilter(faultTreeUri, filter);
    }

    @GetMapping(value = "/check-service", produces = {MediaType.TEXT_PLAIN_VALUE})
    public String checkOperationalDataService(){
        return operationalDataService.checkConnection();
    }
}
