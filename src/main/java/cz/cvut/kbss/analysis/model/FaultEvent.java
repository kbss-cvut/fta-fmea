package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.model.util.EventType;
import cz.cvut.kbss.analysis.model.util.GateType;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_FaultEvent)
@Data
public class FaultEvent extends AbstractEntity {

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasFaultEventType)
    private EventType eventType;

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasName)
    private String name;

    @OWLDataProperty(iri = Vocabulary.s_p_hasDescription)
    private String description;

    @ParticipationConstraints(nonEmpty = true)
    @OWLObjectProperty(iri = Vocabulary.s_p_hasRPN, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private RiskPriorityNumber riskPriorityNumber;

    @OWLDataProperty(iri = Vocabulary.s_p_hasGateType)
    private GateType gateType;

    @OWLDataProperty(iri = Vocabulary.s_p_hasProbability)
    private Double probability;

    @OWLObjectProperty(iri = Vocabulary.s_p_hasChildren, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<FaultEvent> children = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_hasFailureMode, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private FailureMode failureMode;

    public void addChild(FaultEvent child) {
        getChildren().add(child);
    }


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
