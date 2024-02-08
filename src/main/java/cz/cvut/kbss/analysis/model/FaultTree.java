package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_FaultTree)
@Getter
@Setter
public class FaultTree extends NamedEntity {

    @NotNull(message = "Manifesting event must be chosen")
    @ParticipationConstraints(nonEmpty = true)
    @OWLObjectProperty(iri = Vocabulary.s_p_isManifestedBy, cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private FaultEvent manifestingEvent;

    @OWLObjectProperty(iri = Vocabulary.s_p_hasFailureModesTable, cascade = CascadeType.ALL)
    private FailureModesTable failureModesTable;

    @OWLObjectProperty(iri = Vocabulary.s_p_has_scenario)
    private Set<FaultEventScenario> faultEventScenarios;

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
