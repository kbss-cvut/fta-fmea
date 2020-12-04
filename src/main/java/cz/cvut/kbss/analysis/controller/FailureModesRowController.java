package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.dto.update.FailureModesRowRpnUpdateDTO;
import cz.cvut.kbss.analysis.service.FailureModesRowRepositoryService;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/failureModesRow")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FailureModesRowController {

    private final FailureModesRowRepositoryService repositoryService;

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE})
    public void update(@RequestBody FailureModesRowRpnUpdateDTO rowRpnUpdateDTO) {
        log.info("> update - {}", rowRpnUpdateDTO);

        repositoryService.updateByDTO(rowRpnUpdateDTO);
    }

}
