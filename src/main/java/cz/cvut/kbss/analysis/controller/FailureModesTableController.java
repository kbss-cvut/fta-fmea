package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.dto.table.FailureModesTableDataDTO;
import cz.cvut.kbss.analysis.dto.update.FailureModesTableUpdateDTO;
import cz.cvut.kbss.analysis.model.FailureModesTable;
import cz.cvut.kbss.analysis.security.SecurityConstants;
import cz.cvut.kbss.analysis.service.FailureModesTableRepositoryService;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/failureModesTable")
@PreAuthorize("hasRole('" + SecurityConstants.ROLE_USER + "')")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FailureModesTableController {

    private final FailureModesTableRepositoryService repositoryService;
    private final IdentifierService identifierService;

    @GetMapping
    public List<FailureModesTable> findAll() {
        return repositoryService.findAll();
    }

    @GetMapping("/summaries")
    public List<FailureModesTable> summaries() {
        return repositoryService.findAllSummaries();
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

        URI tableUri = identifierService.composeIdentifier(Vocabulary.s_c_failure_modes_table, failureModeTableFragment);
        repositoryService.remove(tableUri);
    }

    @GetMapping(value = "/{failureModeTableFragment}/computeTableData")
    public FailureModesTableDataDTO computeTableData(@PathVariable(name = "failureModeTableFragment") String failureModeTableFragment) {
        log.info("> computeTableData - {}", failureModeTableFragment);

        URI tableUri = identifierService.composeIdentifier(Vocabulary.s_c_failure_modes_table, failureModeTableFragment);
        return repositoryService.computeTableData(tableUri);
    }

    @GetMapping(value = "/{failureModeTableFragment}/export", produces = "text/csv")
    public String export(@PathVariable(name = "failureModeTableFragment") String failureModeTableFragment, HttpServletResponse response) {
        log.info("> export - {}", failureModeTableFragment);

        URI tableUri = identifierService.composeIdentifier(Vocabulary.s_c_failure_modes_table, failureModeTableFragment);

        response.setContentType("text/csv");
        response.addHeader("Content-Disposition", "attachment; filename=\"table.csv\"");
        return repositoryService.export(tableUri);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public FailureModesTable createTableAggregate(@RequestBody FailureModesTable failureModesTable) {
        log.info("> createTableAggregate - {}", failureModesTable);

        repositoryService.persist(failureModesTable);
        log.info("< createTableAggregate - {}", failureModesTable);
        return failureModesTable;
    }

}
