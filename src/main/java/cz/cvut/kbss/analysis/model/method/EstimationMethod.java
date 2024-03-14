package cz.cvut.kbss.analysis.model.method;

import cz.cvut.kbss.analysis.model.NamedEntity;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;

@OWLClass(iri = Vocabulary.s_c_failure_rate_general_estimation_method)
public class EstimationMethod extends NamedEntity {
    @OWLDataProperty(iri = Vocabulary.s_p_code)
    public String code;
}
