package cz.cvut.kbss.analysis.model.util;

import cz.cvut.kbss.jopa.model.EntityManager;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Exporter {
    public static void exportAsTrig(EntityManager em, String file){
        Repository r = em.unwrap(Repository.class);
        RepositoryConnection c = r.getConnection();
        try(Writer w = new FileWriter(file)) {
            c.export(Rio.createWriter(RDFFormat.TRIG, w));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
