package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_failure_modes_row)
@Getter
@Setter
public class FailureModesRow extends AbstractEntity {

    public FailureModesRow() {
    }

    public FailureModesRow(FailureModesRow failureModesRow) {
        this.setEffects(failureModesRow.getEffects());
        this.setLocalEffect(failureModesRow.getLocalEffect());
        this.setFinalEffect(failureModesRow.getFinalEffect());
        this.setRiskPriorityNumber(failureModesRow.getRiskPriorityNumber());
        this.setMitigation(failureModesRow.getMitigation());
    }

    @NotNull(message = "Final Effect must be chosen")
    @ParticipationConstraints(nonEmpty = true)
    @OWLObjectProperty(iri = Vocabulary.s_p_has_final_effect, fetch = FetchType.EAGER)
    private URI finalEffect;

    @NotNull(message = "Local Effect must be chosen")
    @ParticipationConstraints(nonEmpty = true)
    @OWLObjectProperty(iri = Vocabulary.s_p_has_local_effect, fetch = FetchType.EAGER)
    private URI localEffect;

    @OWLObjectProperty(iri = Vocabulary.s_p_has_effect, fetch = FetchType.EAGER)
    private Set<URI> effects;

    @OWLObjectProperty(iri = Vocabulary.s_p_has_rpn, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private RiskPriorityNumber riskPriorityNumber;

    @OWLObjectProperty(iri = Vocabulary.s_p_has_mitigation, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Mitigation mitigation;

    @Override
    public String toString() {
        return "FailureModesRow <" + getUri() + "/>";
    }
}
