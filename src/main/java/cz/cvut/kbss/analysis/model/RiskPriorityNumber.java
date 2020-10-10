package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.ParticipationConstraints;
import lombok.Data;
import lombok.EqualsAndHashCode;

@OWLClass(iri = Vocabulary.s_c_RiskPriorityNumber)
@EqualsAndHashCode(callSuper = true)
@Data
public class RiskPriorityNumber extends AbstractEntity {

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasProbability)
    private Double probability;

    @OWLDataProperty(iri = Vocabulary.s_p_hasSeverity)
    private Integer severity;

    @OWLDataProperty(iri = Vocabulary.s_p_hasDetection)
    private Integer detection;

}
