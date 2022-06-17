package cz.cvut.kbss.analysis.controller;


import cz.cvut.kbss.analysis.model.Document;
import cz.cvut.kbss.analysis.service.external.AnnotatorService;
import cz.cvut.kbss.jsonld.JsonLd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/documents")
@RestController
public class AnnotatorController {

    private final AnnotatorService annotatorService;

    @Autowired
    public AnnotatorController(AnnotatorService annotatorService) {
        this.annotatorService = annotatorService;
    }

    @GetMapping(produces = {JsonLd.MEDIA_TYPE, MediaType.APPLICATION_JSON_VALUE})
    public String getDocuments(){
        return annotatorService.getDocuments();
    }

}
