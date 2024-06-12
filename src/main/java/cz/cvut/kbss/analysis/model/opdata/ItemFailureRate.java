package cz.cvut.kbss.analysis.model.opdata;

import cz.cvut.kbss.jopa.model.annotations.*;

import java.net.URI;


@OWLClass(iri = "http://item-failure-rate")
public class ItemFailureRate {

    @Id
    protected URI uri;

    @OWLDataProperty(iri = "http://failureRate")
    protected Double failureRate;

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public Double getFailureRate() {
        return failureRate;
    }

    public void setFailureRate(Double failureRate) {
        this.failureRate = failureRate;
    }
}
