package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.model.opdata.OperationalDataFilter;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_fault_tree)
@Getter
@Setter
public class FaultTree extends ManagedEntity {

    @Transient
    @OWLObjectProperty(iri = Vocabulary.s_p_is_artifact_of)
    protected NamedEntity system;
    @Transient
    @OWLObjectProperty(iri = Vocabulary.s_p_is_performed_by)
    protected NamedEntity subsystem;

    @Transient
    @OWLDataProperty(iri = Vocabulary.s_p_required_failure_rate)
    protected Double requiredFailureRate;

    @Transient
    @OWLDataProperty(iri = Vocabulary.s_p_calculated_failure_rate)
    protected Double calculatedFailureRate;

    @Transient
    @OWLDataProperty(iri = Vocabulary.s_p_fha_based_failure_rate)
    protected Double fhaBasedFailureRate;

    @Transient
    @OWLObjectProperty(iri = Vocabulary.s_p_has_operational_data_filter)
    protected OperationalDataFilter operationalDataFilter;

    @NotNull(message = "Manifesting event must be chosen")
    @ParticipationConstraints(nonEmpty = true)
    @OWLObjectProperty(iri = Vocabulary.s_p_is_manifested_by, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private FaultEvent manifestingEvent;

    @OWLObjectProperty(iri = Vocabulary.s_p_has_failure_modes_table, cascade = CascadeType.ALL)
    private FailureModesTable failureModesTable;

    @OWLObjectProperty(iri = Vocabulary.s_p_has_scenario, cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    private Set<FaultEventScenario> faultEventScenarios;

    public Set<FaultEvent> getAllEvents(){
        return Optional.ofNullable(getManifestingEvent())
                .filter(e -> e != null)
                .map(e -> e.getAllEventParts()).orElse(new HashSet<>());
    }

    @Override
    public void setAs(NamedEntity namedEntity) {
        if(namedEntity instanceof FaultTreeSummary)
            ((FaultTreeSummary)namedEntity).copyTo(this);
        else
            super.setAs(namedEntity);
    }

    @Override
    public String toString() {
        return "FaultTree <" + getUri() + "/>";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FaultTree that = (FaultTree) o;
        return Objects.equals(getUri(), that.getUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri());
    }

}
