package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mapdb.Fun;

import java.util.*;

@OWLClass(iri = Vocabulary.s_c_Function)
@Data
public class Function extends AbstractEntity {

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasName)
    private String name;

    @OWLObjectProperty(iri = Vocabulary.s_p_hasFailureMode, cascade = CascadeType.ALL)
    private Set<FailureMode> failureModes = new HashSet<>();

    public void addFailureMode(FailureMode failureMode) {
        getFailureModes().add(failureMode);
    }

    @Override
    public String toString() {
        return "Function <" + getUri() + "/>";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Function that = (Function) o;
        return Objects.equals(getUri(), that.getUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri());
    }

}

