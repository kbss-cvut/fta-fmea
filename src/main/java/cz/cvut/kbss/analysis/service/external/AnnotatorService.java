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

    public void convertDocument(String docuementIri) throws UnsupportedEncodingException {
        log.info("calling external annotation conversion service <{}> and store result in <{}>", conf.getConvertDocumentAPI(), docuementIri);
        RestTemplate restTemplate = new RestTemplate();
        String url = conf.getConvertDocumentAPI() + "&documentIri={documentIri}";

        ResponseEntity<String> response
                = restTemplate.getForEntity(url, String.class, docuementIri);

        String body = response.getBody();

        StringReader reader = new StringReader(body);
        // use document iri as the context in which to store converted annotations
        rdfDao.persist(reader, docuementIri);
    }

    public  void processAnnotations(){
        try {
            log.info("calling external annotation processing service {}", conf.getProcessAnnotationAPI());
            RestTemplate restTemplate = new RestTemplate();
            String url = conf.getProcessAnnotationAPI();
            ResponseEntity<String> response
                    = restTemplate.getForEntity(url, String.class);
        }catch (Exception e){
            log.warn("Failed executing external process annotation service at <{}>. Error message - {}", e.getMessage());
        }
    }
}