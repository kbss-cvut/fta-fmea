package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@OWLClass(iri = Vocabulary.s_c_Gate)
@EqualsAndHashCode(callSuper = true)
@Data
public class Gate extends Event {

    @OWLDataProperty(iri = Vocabulary.s_p_hasGateType)
    private String gateType;

}
