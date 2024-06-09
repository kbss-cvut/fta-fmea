package cz.cvut.kbss.analysis.model.util;

import cz.cvut.kbss.jopa.model.EntityManager;
import org.apache.commons.io.IOUtils;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Anonymizer {

    protected final String VALUES = "\\$\\$VALUES\\$\\$";
    protected String updatePartNumbersTemplate;
    protected String updateStock;
    protected String updateEventTypeLabels;
    protected String updateFailureRates;



    public Anonymizer() {
        loadTemplateUpdates();
    }

    protected String load(String path){
        try {
            return IOUtils.toString(this.getClass().getResource(path), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void loadTemplateUpdates(){
        updatePartNumbersTemplate = load("/queries/anonymize/replace-part-numbers.sparql");
        updateStock = load("/queries/anonymize/replace-stock-codes.sparql");
        updateEventTypeLabels = load("/queries/anonymize/replace-fault-event-and-failure-mode-names.sparql");
        updateFailureRates = load("/queries/anonymize/replace-failure-rates.sparql");
    }

    public void anonymize(EntityManager em, SensitiveProperties sensitiveProperties){
        Repository r = em.unwrap(Repository.class);
        RepositoryConnection c = r.getConnection();

        for(String update : prepareQueries(sensitiveProperties)){
            c.prepareUpdate(update).execute();
        }
    }



    protected List<String> prepareQueries(SensitiveProperties sensitiveProperties){
        return Arrays.asList(
                preparePartNumberUpdate(sensitiveProperties.getPartNumberSet()),
                prepareStockUpdate(sensitiveProperties.getStockSet()),
                prepareNamesUpdate(sensitiveProperties.getFaultEventNamesSet()),
                prepareFailureRateUpdate(sensitiveProperties.getFailureRateSet())
        );
    }

    protected String preparePartNumberUpdate(Set<String> partNumbers){
        List<String> l = new ArrayList<>(partNumbers);
        return updatePartNumbersTemplate.replaceFirst(VALUES, IntStream.range(0, l.size())
                .mapToObj(i -> String.format("(\"%s\" \"pn-%d\")", l.get(i), i))
                .collect(Collectors.joining("\n")));
    }

    protected String prepareStockUpdate(Set<String> stock){
        List<String> l = new ArrayList<>(stock);
        return updatePartNumbersTemplate.replaceFirst(VALUES, IntStream.range(0, l.size())
                .mapToObj(i -> String.format("(\"%s\" \"stock-%d\")", l.get(i), i))
                .collect(Collectors.joining("\n")));
    }

    protected String prepareNamesUpdate(Set<String> faultEventNames){
        return updateEventTypeLabels;
    }

    protected String prepareFailureRateUpdate(Set<String> failureRates){
        return updateFailureRates;
    }

    protected void executeUpdates(){

    }


    protected void loadQueries(){

    }
}
