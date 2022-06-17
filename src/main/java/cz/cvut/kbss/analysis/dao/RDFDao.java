package cz.cvut.kbss.analysis.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.analysis.config.conf.AnnotatorConf;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

@Repository
public class RDFDao {
    protected final EntityManager em;

    @Autowired
    public RDFDao(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public void persist(Reader rdfData, String contextIRI){
        org.eclipse.rdf4j.repository.Repository r = ((EntityManager)em.getDelegate()).unwrap(org.eclipse.rdf4j.repository.Repository.class);
        if(! r.isInitialized() || !r.getConnection().isOpen()){
            throw new RuntimeException("Cannot persist converted annotations in document.");
        }
        RepositoryConnection c = r.getConnection();
        boolean handleTransaction = false;
        try {
            if(!c.isActive()) {
                handleTransaction = true;
                c.begin();
            }
            c.add(rdfData, null, RDFFormat.JSONLD, contextIRI == null ? null : r.getValueFactory().createIRI(contextIRI));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (handleTransaction)
                c.commit();
        }
    }
}
