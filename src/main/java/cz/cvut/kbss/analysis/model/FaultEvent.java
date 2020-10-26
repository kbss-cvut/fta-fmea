package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.model.util.EventType;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@OWLClass(iri = Vocabulary.s_c_FaultEvent)
@Data
public class FaultEvent extends Event {

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasFaultEventType)
    private EventType eventType;

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasName)
    private String name;

    @OWLDataProperty(iri = Vocabulary.s_p_hasDescription)
    private String description;

    @ParticipationConstraints(nonEmpty = true)
    @OWLObjectProperty(iri = Vocabulary.s_p_hasRPN, cascade = CascadeType.ALL)
    private RiskPriorityNumber riskPriorityNumber;

    @OWLObjectProperty(iri = Vocabulary.s_p_isPreventedBy, cascade = CascadeType.ALL)
    private TakenAction takenAction;

    @Override
    public String toString() {
        return "FaultEvent <" + getUri() + "/>";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FaultEvent that = (FaultEvent) o;
        return getUri().equals(that.getUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri());
    }
}
