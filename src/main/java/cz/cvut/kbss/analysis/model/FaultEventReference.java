package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;

@SparqlResultSetMapping(name = "FaultEventReference", classes =
@ConstructorResult(targetClass=FaultEventReference.class, variables={
        @VariableResult(name="faultEvent"),
        @VariableResult(name="faultTree")
})
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

    @Transient
    @OWLDataProperty(iri = Vocabulary.s_p_is_part_of)
    protected URI faultTree;
}
