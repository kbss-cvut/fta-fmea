package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.model.method.EstimationMethod;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.CascadeType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import lombok.Getter;
import lombok.Setter;

@OWLClass(iri = Vocabulary.s_c_failure_rate_estimate)
@Getter
@Setter
public class FailureRateEstimate extends AnalysisProduct{

    @OWLDataProperty(iri = Vocabulary.s_p_value)
    protected Double value;

    /**
     * The estimation method
     */
    @OWLObjectProperty(iri = Vocabulary.s_p_is_based_on, cascade = CascadeType.ALL)
    protected EstimationMethod estimationMethod;

}
