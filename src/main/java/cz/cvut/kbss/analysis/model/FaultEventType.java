package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;

@OWLClass(iri = Vocabulary.s_c_fault_event_type)
@Getter
@Setter
public class FaultEventType extends Event{

    @OWLObjectProperty(iri = Vocabulary.s_p_has_failure_rate, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private FailureRate failureRate;

    @Transient
    @OWLDataProperty(iri = Vocabulary.s_p_is_performed_by)
    private String componentName;

    @Transient
    @OWLDataProperty(iri = Vocabulary.s_p_fault_event_type)
    private String eventType;

    @OWLDataProperty(iri = Vocabulary.s_p_auxiliary)
    private Boolean auxiliary;

    @Override
    public void setAs(NamedEntity namedEntity) {
        if(namedEntity instanceof FaultEventTypeSummary)
            ((FaultEventTypeSummary)namedEntity).copyTo(this);
        else
            super.setAs(namedEntity);
    }

}
