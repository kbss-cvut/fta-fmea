package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_FailureModesTable)
@Data
public class FailureModesTable extends AbstractEntity {

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasName)
    private String name;

    @ParticipationConstraints(nonEmpty = true)
    @OWLObjectProperty(iri = Vocabulary.s_p_isDerivedFrom, fetch = FetchType.EAGER)
    private FaultTree faultTree;

    @OWLObjectProperty(iri = Vocabulary.s_p_hasRow, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<FailureModesRow> rows = new HashSet<>();

    @Override
    public String toString() {
        return "FailureModesTable <" + getUri() + "/>";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FailureModesTable that = (FailureModesTable) o;
        return Objects.equals(getUri(), that.getUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri());
    }

}