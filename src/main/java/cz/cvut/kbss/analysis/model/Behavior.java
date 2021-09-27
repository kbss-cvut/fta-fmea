package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_Behavior)
@Getter
@Setter
public class Behavior extends AbstractEntity {

    @OWLObjectProperty(iri = Vocabulary.s_p_activatedBy)
    private Set<Situation> activatedBySituations = new HashSet<>();

    public void addSituation(Situation situation){
        situation.getBehaviors().add(this);
        getActivatedBySituations().add(situation);
    }
}
