package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.model.fta.FtaEventType;
import cz.cvut.kbss.analysis.model.fta.GateType;
import cz.cvut.kbss.analysis.model.diagram.Rectangle;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.*;

@OWLClass(iri = Vocabulary.s_c_FaultEvent)
@Getter
@Setter
public class FaultEvent extends Event {

    /**
     * Use this factory method to create a FaultEvent with a rectangle. Rectangle cannot be initialized because it will cause
     * @return
     */
    public static FaultEvent create(){
        FaultEvent faultEvent = new FaultEvent();
        faultEvent.setRectangle(new Rectangle());
        return faultEvent;
    }

    @NotNull(message = "EventType must be defined")
    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasFaultEventType)
    private FtaEventType eventType;

    @OWLDataProperty(iri = Vocabulary.s_p_hasGateType)
    private GateType gateType;

    @OWLObjectProperty(iri = Vocabulary.s_p_has_rectangle, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Rectangle rectangle;

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
    public FailureMode getFailureMode(){
        return getBehavior() instanceof FailureMode ? (FailureMode) getBehavior() : null;
    }
    public Function getFunction(){
        return getBehavior() instanceof Function ? (Function) getBehavior() : null;
    }

    public void setFailureMode(FailureMode failureMode){
        setBehavior(failureMode);
    }


    public void addChild(FaultEvent child) {
        getChildren().add(child);
    }
    public void addChildren(Set<FaultEvent> children){getChildren().addAll(children);}
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
