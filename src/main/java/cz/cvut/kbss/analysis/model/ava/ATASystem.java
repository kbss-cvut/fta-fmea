package cz.cvut.kbss.analysis.model.ava;

import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * This entity represents an ATA system category. It can be used as a supertype to categorize an Item.
 */
@OWLClass(iri = Vocabulary.s_c_ata_system)
@Getter
@Setter
public class ATASystem extends Component {

    @OWLDataProperty(iri = Vocabulary.s_p_ata_code, simpleLiteral = true)
    private String ataCode;

}
