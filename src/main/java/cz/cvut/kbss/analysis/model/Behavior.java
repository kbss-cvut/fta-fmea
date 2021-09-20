package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_Behavior)
@MappedSuperclass
@Getter
@Setter
public abstract class Behavior extends AbstractEntity {

    @NotEmpty(message = "Name must not be empty")
    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasName)
    private String name;

    @OWLDataProperty(iri = Vocabulary.s_p_hasBehaviorType)
    private BehaviorType behaviorType;

    @OWLObjectProperty(iri = Vocabulary.s_p_requires, fetch = FetchType.EAGER)
    private Set<Behavior> requiredBehaviors = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_impairedBy, fetch = FetchType.EAGER)
    private Set<Behavior> impairedBehaviors = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_hasChildBehavior)
    private Set<Behavior> childBehaviors = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_manifested_by)
    private Set<Event> manifestations = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_hasComponent, fetch = FetchType.EAGER)
    private Component component;

    @OWLObjectProperty(iri = Vocabulary.s_p_activatedBy)
    private Set<Situation> activatedBySituations = new HashSet<>();

    public void addSituation(Situation situation){
        situation.getBehaviors().add(this);
        getActivatedBySituations().add(situation);
    }

    public void addRequiredBehavior(Behavior behavior){getRequiredBehaviors().add(behavior);}
    public void addImpairedBehavior(Behavior behavior){getImpairedBehaviors().add(behavior);}
    public void addChildBehavior(Behavior behavior){getChildBehaviors().add(behavior);}
    public void addManifestationBehavior(Event event){getManifestations().add(event);}

    public void removeImpairedBehavior(Behavior behavior){getImpairedBehaviors().remove(behavior);}
    public void removeRequiredBehavior(Behavior behavior){getRequiredBehaviors().remove(behavior);}
    public void removeChildBehavior(Behavior behavior){getChildBehaviors().remove(behavior);}
    public void removeManifestationBehavior(Event event){getManifestations().remove(event);}
}
