package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;

import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_behavior)
@Getter
@Setter
public abstract class Behavior extends DomainEntity<Behavior> {

    @OWLObjectProperty(iri = Vocabulary.s_p_is_derived_from, fetch = FetchType.EAGER)
    protected Set<Behavior> supertypes;

    @OWLDataProperty(iri = Vocabulary.s_p_behavior_type, simpleLiteral = true)
    private BehaviorType behaviorType = BehaviorType.AtomicBehavior;

    @OWLObjectProperty(iri = Vocabulary.s_p_has_required, fetch = FetchType.EAGER)
    private Set<Behavior> requiredBehaviors = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_is_impairing, fetch = FetchType.EAGER)
    private Set<Behavior> impairedBehaviors = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_has_child_behavior, fetch = FetchType.EAGER)
    private Set<Behavior> childBehaviors = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_is_manifested_by)
    private Set<Event> manifestations = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_has_component, fetch = FetchType.EAGER)
    private Item item;


    @OWLObjectProperty(iri = Vocabulary.s_p_is_activated_by)
    private Set<Situation> activatedBySituations = new HashSet<>();

    public boolean isFailureModeCause() {
        return this instanceof FailureMode
                && ((FailureMode) this).getFailureModeType() == FailureModeType.FailureModeCause;
    }

    public void addSituation(Situation situation){
        situation.getBehaviors().add(this);
        getActivatedBySituations().add(situation);
    }

    public void addRequiredBehavior(Behavior behavior){getRequiredBehaviors().add(behavior);}
    public void addImpairedBehavior(Behavior behavior){getImpairedBehaviors().add(behavior);}
    public void addChildBehavior(Behavior behavior){getChildBehaviors().add(behavior);}
    public void addManifestation(Event event){getManifestations().add(event);}

    public void removeImpairedBehavior(Behavior behavior){getImpairedBehaviors().remove(behavior);}
    public void removeRequiredBehavior(Behavior behavior){getRequiredBehaviors().remove(behavior);}
    public void removeChildBehavior(Behavior behavior){getChildBehaviors().remove(behavior);}
    public void removeManifestation(Event event){getManifestations().remove(event);}
}
