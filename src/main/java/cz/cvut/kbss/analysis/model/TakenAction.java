package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.CascadeType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@OWLClass(iri = Vocabulary.s_c_TakenAction)
@EqualsAndHashCode(callSuper = true)
@Data
public class TakenAction extends AbstractEntity{

    @OWLObjectProperty(iri = Vocabulary.s_p_prevents, cascade = CascadeType.ALL)
    private FaultEvent faultEvent;

}
