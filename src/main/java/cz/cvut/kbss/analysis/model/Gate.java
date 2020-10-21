package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.model.util.GateType;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.CascadeType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_Gate)
@Data
public class Gate extends Event {

    @OWLDataProperty(iri = Vocabulary.s_p_hasGateType)
    private GateType gateType = GateType.OR; // neutral gate type

    // only top gate causes failureMode
    @OWLObjectProperty(iri = Vocabulary.s_p_manifests)
    protected FailureMode failureMode;

    @OWLObjectProperty(iri = Vocabulary.s_p_produces)
    protected FaultEvent producedEvent;

    @OWLObjectProperty(iri = Vocabulary.s_p_consistsOf, cascade = CascadeType.ALL)
    protected Set<FaultEvent> inputEvents;

    public void addInputEvent(FaultEvent inputEvent) {
        if (getInputEvents() == null) {
            setInputEvents(new HashSet<>());
        }
        getInputEvents().add(inputEvent);
    }

    @Override
    public String toString() {
        return "Gate <" + getUri() + "/>";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Gate that = (Gate) o;
        return Objects.equals(getUri(), that.getUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri());
    }

}
