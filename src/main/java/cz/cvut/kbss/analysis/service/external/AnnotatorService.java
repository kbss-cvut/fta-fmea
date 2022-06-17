package cz.cvut.kbss.analysis.service.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.analysis.config.conf.AnnotatorConf;
import cz.cvut.kbss.analysis.model.AbstractEntity;
import cz.cvut.kbss.jsonld.JsonLd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnnotatorService {
    private final AnnotatorConf conf;
    private final RestTemplate restTemplate;

    @Autowired
    public AnnotatorService(AnnotatorConf conf, RestTemplate restTemplate) {
        this.conf = conf;
        this.restTemplate = restTemplate;
    }

    public String getDocuments(){
        ResponseEntity<String> response
                = restTemplate.getForEntity(conf.getListDocumentsAPI(), String.class);
        return response.getBody();
    }
}