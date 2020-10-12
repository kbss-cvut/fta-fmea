package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@OWLClass(iri = Vocabulary.s_c_FailureMode)
public class FailureMode extends AbstractEntity {

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasName)
    private String name;

    @OWLDataProperty(iri = Vocabulary.s_p_hasDescription)
    private String description;

    @OWLObjectProperty(iri = Vocabulary.s_p_hasRPN, cascade = CascadeType.ALL)
    private RiskPriorityNumber riskPriorityNumber;

    @OWLObjectProperty(iri = Vocabulary.s_p_isCausedBy, cascade = CascadeType.ALL)
    private Event causingEvent;

    @OWLObjectProperty(iri = Vocabulary.s_p_isMitigatedBy, cascade = CascadeType.ALL)
    private Set<Mitigation> mitigation;

}
