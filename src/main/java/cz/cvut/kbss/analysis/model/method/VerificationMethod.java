package cz.cvut.kbss.analysis.model.method;

import cz.cvut.kbss.analysis.model.NamedEntity;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import lombok.Getter;
import lombok.Setter;

@OWLClass(iri = Vocabulary.s_c_verification_method)
@Getter
@Setter
public class VerificationMethod extends NamedEntity {
    @OWLDataProperty(iri = Vocabulary.s_p_code)
    public String code;
}
