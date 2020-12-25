package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.model.util.EventType;
import cz.cvut.kbss.analysis.model.util.GateType;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.util.*;

@OWLClass(iri = Vocabulary.s_c_FaultEvent)
@Getter
@Setter
public class FaultEvent extends AbstractEntity {

    @NotNull(message = "EventType must be defined")
    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasFaultEventType)
    private EventType eventType;

    @NotEmpty(message = "Name must not be empty")
    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasName)
    private String name;

    @OWLDataProperty(iri = Vocabulary.s_p_hasDescription)
    private String description;

    @OWLDataProperty(iri = Vocabulary.s_p_hasGateType)
    private GateType gateType;

    @OWLDataProperty(iri = Vocabulary.s_p_hasProbability)
    private Double probability;

    @OWLObjectProperty(iri = Vocabulary.s_p_hasChildren, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<FaultEvent> children = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_hasFailureMode, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private FailureMode failureMode;

    @OWLDataProperty(iri = Vocabulary.s_p_hasSequenceProbability)
    private Double sequenceProbability;

    @Sequence
    @OWLObjectProperty(iri = Vocabulary.s_p_hasChildrenSequence, fetch = FetchType.EAGER)
    private List<URI> childrenSequence = new ArrayList<>();

    public void addChild(FaultEvent child) {
        getChildren().add(child);
    }

    public void addChildSequenceUri(URI childUri) {getChildrenSequence().add(childUri);}

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
