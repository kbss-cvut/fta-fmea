package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_System)
@Data
public class System extends AbstractEntity {

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasName)
    private String name;

    @OWLObjectProperty(iri = Vocabulary.s_p_hasComponent, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Component> components = new HashSet<>();

    public void addComponent(Component component) {
        getComponents().add(component);
    }

    @Override
    public String toString() {
        return "System <" + getUri() + "/>";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        System that = (System) o;
        return getUri().equals(that.getUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri());
    }

}
