package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.FetchType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_fault_event_scenario_type)
@Getter
@Setter
public class FaultEventScenario extends AnalysisProduct {

    @OWLObjectProperty(iri = Vocabulary.s_p_has_part, fetch = FetchType.EAGER)
    private Set<FaultEvent> scenarioParts;

    @OWLDataProperty(iri = Vocabulary.s_p_hasProbability)
    private Double probability;


    public FaultEventScenario() {
    }

    public FaultEventScenario(Set<FaultEvent> scenarioParts) {
        this.scenarioParts = scenarioParts;
    }

    public void updateProbability(){
        setProbability(calculateProbability());
    }

    public Double calculateProbability(){
        Double prob = 1.;
        for(FaultEvent part : scenarioParts){
            prob = prob * part.getProbability();
        }
        return prob;
    }

    public boolean isEmptyScenario(){
        return scenarioParts == null || scenarioParts.isEmpty();
    }
}
