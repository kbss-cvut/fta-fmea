package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_Component)
@Data
public class Component extends AbstractEntity {

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasName)
    private String name;

    @OWLObjectProperty(iri = Vocabulary.s_p_hasFunction, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Function> functions = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_hasFailureMode, cascade = CascadeType.ALL)
    private Set<FailureMode> failureModes = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_isPartOf, cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private Component parentComponent;

    public void addFunction(Function function) {
        getFunctions().add(function);
    }

    public void addFailureMode(FailureMode failureMode) {
        getFailureModes().add(failureMode);
    }

    @Override
    public String toString() {
        return "Component <" + getUri() + "/>";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Component that = (Component) o;
        return getUri().equals(that.getUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri());
    }
}
