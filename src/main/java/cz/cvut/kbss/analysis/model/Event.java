package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.CascadeType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_Event)
@Getter
@Setter
public class Event extends AbstractEntity {

    @OWLObjectProperty(iri = Vocabulary.s_p_manifestationOf, cascade = CascadeType.ALL)
    private Behavior behavior;

    @OWLObjectProperty(iri = Vocabulary.s_p_bringsAbout)
    private Set<Situation> bringsAboutSituations = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_triggeredBy)
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
