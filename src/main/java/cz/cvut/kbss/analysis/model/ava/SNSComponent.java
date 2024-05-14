package cz.cvut.kbss.analysis.model.ava;

import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;


/*
 * This entity represents component from an SNS partonomy. The SNSComponent categorized as a IndependentSNSItem Kind and
 * or an ATASystem.
 */
@OWLClass(iri = Vocabulary.s_c_sns_component)
@Getter
@Setter
public class SNSComponent extends Component {
    @OWLDataProperty(iri = Vocabulary.s_p_quantity)
    private Integer quantity;

    @OWLDataProperty(iri = Vocabulary.s_p_schematic_designation, simpleLiteral = true)
    private Set<String> schematicDescription;

}
