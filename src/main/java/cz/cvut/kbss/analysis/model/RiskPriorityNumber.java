package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.ParticipationConstraints;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@OWLClass(iri = Vocabulary.s_c_RiskPriorityNumber)
@Data
public class RiskPriorityNumber extends AbstractEntity {

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasProbability)
    private Double probability;

    @OWLDataProperty(iri = Vocabulary.s_p_hasSeverity)
    private Integer severity;

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
