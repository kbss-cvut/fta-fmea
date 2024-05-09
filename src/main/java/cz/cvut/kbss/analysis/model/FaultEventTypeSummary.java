package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;

@SparqlResultSetMappings(
        @SparqlResultSetMapping(name="FaultEventSummary", entities = {
                @EntityResult(entityClass=FaultEventTypeSummary.class)
        })
)
@OWLClass(iri = Vocabulary.s_c_fault_event_type)
@Getter
@Setter
public class FaultEventTypeSummary extends NamedEntity{

    @OWLDataProperty(iri = Vocabulary.s_p_is_performed_by)
    private String componentName;

    @OWLDataProperty(iri = Vocabulary.s_p_fault_event_type)
    private String eventType;


//    private

    public void copyTo(FaultEventType faultEventType){
        super.copyTo(faultEventType);
        faultEventType.setComponentName(componentName);
        faultEventType.setEventType(eventType);
    }
}
