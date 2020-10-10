package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.model.util.HasIdentifier;
import cz.cvut.kbss.analysis.model.util.HasTypes;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;

import java.io.Serializable;
import java.net.URI;
import java.util.Objects;
import java.util.Set;

@MappedSuperclass
@Data
public abstract class FailureEffect implements HasIdentifier, HasTypes, Serializable {

    @Id(generated = true)
    private URI uri;

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasName)
    protected String name;

    @OWLDataProperty(iri = Vocabulary.s_p_hasDescription)
    protected String description;

    @Types
    protected Set<String> types;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FailureEffect that = (FailureEffect) o;
        return Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }
}
