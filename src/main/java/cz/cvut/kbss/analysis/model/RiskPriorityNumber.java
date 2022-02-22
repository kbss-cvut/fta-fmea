package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.ParticipationConstraints;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@OWLClass(iri = Vocabulary.s_c_RiskPriorityNumber)
@Getter
@Setter
public class RiskPriorityNumber extends AbstractEntity {

    @Min(value = 1, message = "Severity must not be lower than 1")
    @Max(value = 10, message = "Severity must not be greater than 10")
    @OWLDataProperty(iri = Vocabulary.s_p_hasSeverity)
    private Integer severity;

    @Min(value = 1, message = "Occurrence must not be lower than 1")
    @Max(value = 10, message = "Occurrence must not be greater than 10")
    @OWLDataProperty(iri = Vocabulary.s_p_hasOccurrence)
    private Integer occurrence;

    @Min(value = 1, message = "Detection must not be lower than 1")
    @Max(value = 10, message = "Detection must not be greater than 10")
    @OWLDataProperty(iri = Vocabulary.s_p_hasDetection)
    private Integer detection;

    @Override
    public String toString() {
        return "RiskPriorityNumber <" + getUri() + "/>";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RiskPriorityNumber that = (RiskPriorityNumber) o;
        return Objects.equals(getUri(), that.getUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri());
    }

}
