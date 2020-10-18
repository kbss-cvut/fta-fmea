package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.persistence.util.HasAuthorDataManager;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_FailureMode)
@EntityListeners(HasAuthorDataManager.class)
@Data
public class FailureMode extends HasAuthorData {

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasName)
    private String name;

    @OWLDataProperty(iri = Vocabulary.s_p_hasDescription)
    private String description;

    @OWLObjectProperty(iri = Vocabulary.s_p_hasRPN, cascade = CascadeType.ALL)
    private RiskPriorityNumber riskPriorityNumber;

    // in case of FTA, top event is FailureMode itself. For FMEA, gate structure will be flattened.
    @OWLObjectProperty(iri = Vocabulary.s_p_isCausedBy, cascade = CascadeType.ALL)
    private Gate causingGate;

    @OWLObjectProperty(iri = Vocabulary.s_p_isMitigatedBy, cascade = CascadeType.ALL)
    private Set<Mitigation> mitigation;

    public void addMitigation(Mitigation mitigation) {
        if (getMitigation() == null) {
            setMitigation(new HashSet<>());
        }
        getMitigation().add(mitigation);
    }

    @Override
    public String toString() {
        return "FailureMode <" + getUri() + "/>";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FailureMode that = (FailureMode) o;
        return Objects.equals(getUri(), that.getUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri());
    }

}
