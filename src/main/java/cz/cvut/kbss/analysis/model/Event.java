package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.CascadeType;
import cz.cvut.kbss.jopa.model.annotations.FetchType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_event)
@Getter
@Setter
public class Event extends DomainEntity<Event> {

    @OWLObjectProperty(iri = Vocabulary.s_p_is_derived_from, fetch = FetchType.EAGER)
    protected Set<Event> supertypes;

    @OWLObjectProperty(iri = Vocabulary.s_p_is_part_of, fetch = FetchType.EAGER)
    protected Set<Event> contextEvents;

    @OWLObjectProperty(iri = Vocabulary.s_p_is_manifestation_of, cascade = CascadeType.ALL)
    private Behavior behavior;

    @OWLObjectProperty(iri = Vocabulary.s_p_is_bringing_about)
    private Set<Situation> bringsAboutSituations = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_is_triggered_by)
    private Set<Situation> triggeredBySituations = new HashSet<>();

    public void setBehavior(Behavior behavior){
        Behavior lastBehavior = this.behavior;
        this.behavior = behavior;
        if(behavior == null && lastBehavior != null){
            lastBehavior.removeManifestation(this);
        }else if(behavior != null){
            behavior.addManifestation(this);
        }
    }

    public void addBringsAboutSituation(Situation situation){
        situation.getBroughtAboutByEvents().add(this);
        getBringsAboutSituations().add(situation);
    }

    public void addTriggeredBySituation(Situation situation){
        situation.getTriggersEvents().add(this);
        getTriggeredBySituations().add(situation);
    }
}
