package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Objects;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_FailureModesRow)
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
    @OWLObjectProperty(iri = Vocabulary.s_p_hasFinalEffect, fetch = FetchType.EAGER)
    private URI finalEffect;

    @NotNull(message = "Local Effect must be chosen")
    @ParticipationConstraints(nonEmpty = true)
    @OWLObjectProperty(iri = Vocabulary.s_p_hasLocalEffect, fetch = FetchType.EAGER)
    private URI localEffect;

    @OWLObjectProperty(iri = Vocabulary.s_p_hasEffect, fetch = FetchType.EAGER)
    private Set<URI> effects;

    @OWLObjectProperty(iri = Vocabulary.s_p_hasRPN, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private RiskPriorityNumber riskPriorityNumber;

    @OWLObjectProperty(iri = Vocabulary.s_p_hasMitigation, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Mitigation mitigation;

    @Override
    public String toString() {
        return "FailureModesRow <" + getUri() + "/>";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FailureModesRow that = (FailureModesRow) o;
        return Objects.equals(getUri(), that.getUri()) && Objects.equals(localEffect, that.getLocalEffect());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri());
    }

}
