package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@OWLClass(iri = Vocabulary.s_c_FaultEvent)
@EqualsAndHashCode(callSuper = true)
@Data
public class FaultEvent extends Event {

    @OWLDataProperty(iri = Vocabulary.s_p_hasFaultEventType)
    private String eventType;

}
