package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;

@SparqlResultSetMapping(name = "FaultEventReference", entities =
        {@EntityResult(entityClass=FaultEventReference.class)}
)
@OWLClass(iri = Vocabulary.s_c_fault_event)
@Getter
@Setter
public class FaultEventReference {

    public FaultEventReference() {
    }

    public FaultEventReference(URI faultEvent, URI faultTree) {
        this.faultEvent = faultEvent;
        this.faultTree = faultTree;
    }

    @Id
    protected URI faultEvent;

    @OWLDataProperty(iri = Vocabulary.s_p_is_part_of)
    protected URI faultTree;

    @OWLDataProperty(iri = Vocabulary.s_p_probability)
    protected Double probability;

    @OWLDataProperty(iri = Vocabulary.s_p_status)
    protected String status;
}
