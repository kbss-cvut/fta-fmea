package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.dto.table.FailureModesTableDataDTO;
import cz.cvut.kbss.analysis.dto.update.FailureModesTableUpdateDTO;
import cz.cvut.kbss.analysis.model.FailureModesTable;
import cz.cvut.kbss.analysis.service.FailureModesTableRepositoryService;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/failureModesTable")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FailureModesTableController {

    private final FailureModesTableRepositoryService repositoryService;
    private final IdentifierService identifierService;

    @GetMapping
    public List<FailureModesTable> findAll() {
        return repositoryService.findAll();
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FailureModesTable update(@RequestBody FailureModesTableUpdateDTO tableUpdateDTO) {
        log.info("> update - {}", tableUpdateDTO);

        FailureModesTable updatedTable = repositoryService.updateByDTO(tableUpdateDTO);

        log.info("< update - {}", updatedTable);
        return updatedTable;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(value = "/{failureModeTableFragment}")
    public void delete(@PathVariable(name = "failureModeTableFragment") String failureModeTableFragment) {
        log.info("> delete - {}", failureModeTableFragment);

        URI tableUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureModesTable, failureModeTableFragment);
        repositoryService.remove(tableUri);
    }

    @GetMapping(value = "/{failureModeTableFragment}/computeTableData")
    public FailureModesTableDataDTO computeTableData(@PathVariable(name = "failureModeTableFragment") String failureModeTableFragment) {
        log.info("> computeTableData - {}", failureModeTableFragment);

        URI tableUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureModesTable, failureModeTableFragment);
        return repositoryService.computeTableData(tableUri);
    }

    @GetMapping(value = "/{failureModeTableFragment}/export", produces = "text/csv")
    public String export(@PathVariable(name = "failureModeTableFragment") String failureModeTableFragment, HttpServletResponse response) {
        log.info("> export - {}", failureModeTableFragment);

        URI tableUri = identifierService.composeIdentifier(Vocabulary.s_c_FailureModesTable, failureModeTableFragment);

        response.setContentType("text/csv");
        response.addHeader("Content-Disposition", "attachment; filename=\"table.csv\"");
        return repositoryService.export(tableUri);
    }

}