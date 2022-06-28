package cz.cvut.kbss.analysis.controller;

import cz.cvut.kbss.analysis.dto.update.MitigationUpdateDTO;
import cz.cvut.kbss.analysis.model.Mitigation;
import cz.cvut.kbss.analysis.service.MitigationRepositoryService;
import cz.cvut.kbss.jsonld.JsonLd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mitigations")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class MitigationController {

    private final MitigationRepositoryService mitigationRepositoryService;

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, JsonLd.MEDIA_TYPE}, produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public Mitigation update(@RequestBody MitigationUpdateDTO mitigation) {
        log.info("> update - {}", mitigation);
        return mitigationRepositoryService.update(mitigation);
    }
}
