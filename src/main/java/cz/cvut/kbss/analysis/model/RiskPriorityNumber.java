package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@OWLClass(iri = Vocabulary.s_c_risk_priority_number)
@Getter
@Setter
public class RiskPriorityNumber extends AbstractEntity {

    @Min(value = 1, message = "Severity must not be lower than 1")
    @Max(value = 10, message = "Severity must not be greater than 10")
    @OWLDataProperty(iri = Vocabulary.s_p_severity)
    private Integer severity;

    @Min(value = 1, message = "Occurrence must not be lower than 1")
    @Max(value = 10, message = "Occurrence must not be greater than 10")
    @OWLDataProperty(iri = Vocabulary.s_p_occurrence)
    private Integer occurrence;

    @Min(value = 1, message = "Detection must not be lower than 1")
    @Max(value = 10, message = "Detection must not be greater than 10")
    @OWLDataProperty(iri = Vocabulary.s_p_detection)
    private Integer detection;

    @Override
    public String toString() {
        return "RiskPriorityNumber <" + getUri() + "/>";
    }
}
