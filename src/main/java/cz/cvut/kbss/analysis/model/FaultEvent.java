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

@OWLClass(iri = Vocabulary.s_c_fault_event)
@Getter
@Setter
public class FaultEvent extends Event {

    /**
     * Use this factory method to create a FaultEvent with a rectangle.
     *
     * NOTE: Rectangle cannot be initialized in constructor due to issue with parsing fault event with a value for
     * rectangle from JSON-LD.
     * @return returns a new FaultEvent instance with initialized FaultEvent.rectangle field containing a new Rectangle instance.
     */
    public static FaultEvent create(){
        FaultEvent faultEvent = new FaultEvent();
        faultEvent.setRectangle(new Rectangle());
        return faultEvent;
    }

    @NotNull(message = "EventType must be defined")
    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_fault_event_type)
    private FtaEventType eventType;

    @OWLDataProperty(iri = Vocabulary.s_p_gate_type)
    private GateType gateType;

    @OWLObjectProperty(iri = Vocabulary.s_p_has_rectangle, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Rectangle rectangle;

    @OWLDataProperty(iri = Vocabulary.s_p_probability)
    private Double probability;

    @OWLObjectProperty(iri = Vocabulary.s_p_has_selected_estimation, fetch = FetchType.EAGER)
    private FailureRateEstimate selectedEstimate;

    @OWLObjectProperty(iri = Vocabulary.s_p_has_child, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<FaultEvent> children = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_has_failure_mode, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private FailureMode failureMode;

    @OWLDataProperty(iri = Vocabulary.s_p_sequence_probability)
    private Double sequenceProbability;

    @Sequence
    @OWLObjectProperty(iri = Vocabulary.s_p_has_child_sequence, fetch = FetchType.EAGER)
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
}
