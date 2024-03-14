package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.CascadeType;
import cz.cvut.kbss.jopa.model.annotations.FetchType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import lombok.Getter;
import lombok.Setter;

@OWLClass(iri = Vocabulary.s_c_fault_event_type)
@Getter
@Setter
public class FaultEventType extends Event{

    @OWLObjectProperty(iri = Vocabulary.s_p_has_failure_rate, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private FailureRate failureRate;

}
