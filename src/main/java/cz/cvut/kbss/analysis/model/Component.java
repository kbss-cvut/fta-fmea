package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.persistence.util.HasAuthorDataManager;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_Component)
@EntityListeners(HasAuthorDataManager.class)
@Data
public class Component extends HasAuthorData {

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasName)
    private String name;

    @OWLObjectProperty(iri = Vocabulary.s_p_hasFunction, cascade = CascadeType.ALL)
    private Set<Function> functions = new HashSet<>();

    public void addFunction(Function function) {
        if (getFunctions() == null) {
            setFunctions(new HashSet<>());
        }
        getFunctions().add(function);
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
