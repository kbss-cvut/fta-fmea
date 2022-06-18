package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.model.util.HasIdentifier;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.Id;
import cz.cvut.kbss.jopa.model.annotations.MappedSuperclass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import lombok.Data;

import java.io.Serializable;
import java.net.URI;
import java.util.Objects;

@MappedSuperclass
@Data
public abstract class AbstractEntity implements HasIdentifier, Serializable {

    @Id(generated = true)
    private URI uri;

    @OWLDataProperty(iri = Vocabulary.s_p_source)
    private URI annotationSource;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractEntity that = (AbstractEntity) o;
        return Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

    @Override
    public String toString() {
        return "AbstractEntity <" + getUri() + "/>";
    }

}
