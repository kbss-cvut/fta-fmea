package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.model.util.FailureRateType;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import lombok.Getter;
import lombok.Setter;

@OWLClass(iri = Vocabulary.s_c_failure_rate)
@Getter
@Setter
public class FailureRate extends AbstractEntity{

    @OWLObjectProperty(iri = Vocabulary.s_p_has_requirement)
    private FailureRateRequirement requirement;

    @OWLObjectProperty(iri = Vocabulary.s_p_has_estimate)
    private FailureRateEstimate estimate;

    @OWLObjectProperty(iri = Vocabulary.s_p_has_prediction)
    private FailureRateEstimate prediction;

}
