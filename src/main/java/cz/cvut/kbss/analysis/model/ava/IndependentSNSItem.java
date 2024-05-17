package cz.cvut.kbss.analysis.model.ava;

import cz.cvut.kbss.analysis.model.Item;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * This entity represents a Kind (typeCategory should be set to Kind) of independent Item used as a component in the SNS
 * partonomy identified by a partNumber and stock.
 *
 *
 */
@OWLClass(iri = Vocabulary.s_c_reusable_system)
@Getter
@Setter
public class IndependentSNSItem extends Item {
    @OWLDataProperty(iri = Vocabulary.s_p_part_number, simpleLiteral = true)
    private String partNumber;

}
