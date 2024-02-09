package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import lombok.Getter;
import lombok.Setter;

@OWLClass(iri = Vocabulary.s_c_failure_rate_requirement)
@Getter
@Setter
public class FailureRateRequirement extends AbstractEntity {
    @OWLDataProperty(iri = Vocabulary.s_p_to)
    private Double upperBound;


}
