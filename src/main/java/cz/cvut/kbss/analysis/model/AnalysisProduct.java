package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import lombok.Getter;
import lombok.Setter;

@OWLClass(iri = Vocabulary.s_c_analysis_product)
@Getter
@Setter
public class AnalysisProduct extends AbstractEntity{
    @OWLObjectProperty(iri = Vocabulary.s_p_creates)
    private AnalysisEvent creationEvent;

}
