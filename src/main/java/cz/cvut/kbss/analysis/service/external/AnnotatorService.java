package cz.cvut.kbss.analysis.service.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.analysis.config.conf.AnnotatorConf;
import cz.cvut.kbss.analysis.dao.RDFDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class AnnotatorService {
    private final AnnotatorConf conf;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    protected final RDFDao rdfDao;

    @Autowired
    public AnnotatorService(AnnotatorConf conf, RestTemplate restTemplate, ObjectMapper objectMapper, RDFDao rdfDao) {
        this.conf = conf;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.rdfDao = rdfDao;
    }

    public String getDocuments(){
        ResponseEntity<String> response
                = restTemplate.getForEntity(conf.getListDocumentsAPI(), String.class);
        return response.getBody();
    }

    public void convertDocument(String context) throws UnsupportedEncodingException {
        log.info("calling external annotation conversion service <{}> and store result in <{}>", conf.getConvertDocumentAPI(), context);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response
                = restTemplate.getForEntity(conf.getConvertDocumentAPI() + "&documentIri=" + URLEncoder.encode(context, StandardCharsets.UTF_8.toString()), String.class);

        String body = response.getBody();
        StringReader reader = new StringReader(body);
        rdfDao.persist(reader, context);
    }
}