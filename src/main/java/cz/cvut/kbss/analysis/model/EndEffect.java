package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import lombok.Data;
import lombok.EqualsAndHashCode;

@OWLClass(iri = Vocabulary.s_c_EndEffect)
@EqualsAndHashCode(callSuper = true)
@Data
public class EndEffect extends FailureEffect {
}
