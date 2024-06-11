package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.model.opdata.OperationalDataFilter;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.service.OperationalDataFilterService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.net.URI;

@Controller("/operational-data-filter")
@Slf4j
public class OperationalDataFilterController {

    private final OperationalDataFilterService filterService;
    private final IdentifierService identifierService;

    public OperationalDataFilterController(OperationalDataFilterService filterService, IdentifierService identifierService) {
        this.filterService = filterService;
        this.identifierService = identifierService;
    }

    @PutMapping(path="reset", produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public void reset(){
        filterService.removeFilter();
    }

    @PostMapping(value = "/system/{systemFragment}", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public void updateSystemFilter(@PathVariable(name = "systemFragment") String systemFragment, OperationalDataFilter filter){
        log.info("> updateSystemFilter - {} to {}", systemFragment, filter);
        URI systemUri = identifierService.composeIdentifier(Vocabulary.s_c_system, systemFragment);
        filterService.updateSystemFilter(systemUri, filter);
    }

    @PostMapping(value = "/fault-tree/{faultTreeFragment}", consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public void updateFaultTreeFilter(@PathVariable(name = "faultTreeFragment") String faultTreeFragment, OperationalDataFilter filter){
        log.info("> updateFaultTreeFilter - {} to {}", faultTreeFragment, filter);
        URI faultTreeUri = identifierService.composeIdentifier(Vocabulary.s_c_fault_tree, faultTreeFragment);
        filterService.updateFaultTreeFilter(faultTreeUri, filter);
    }
}
