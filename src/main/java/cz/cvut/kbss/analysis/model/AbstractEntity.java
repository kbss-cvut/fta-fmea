package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.model.util.HasIdentifier;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;

import java.io.Serializable;
import java.net.URI;
import java.util.Objects;

@MappedSuperclass
@Data
public abstract class AbstractEntity implements HasIdentifier, Serializable {

    @Transient
    private URI context;

    @Id(generated = true)
    private URI uri;

    @OWLObjectProperty(iri = Vocabulary.s_p_source, fetch = FetchType.EAGER)
    private URI annotationSource;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(! (o instanceof AbstractEntity))
            return false;
        AbstractEntity that = (AbstractEntity) o;
        if(uri == null && that.uri == null)
            return super.equals(o);
        return Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return uri != null ? Objects.hash(uri.toString()) : super.hashCode();
    }

    @Override
    public String toString() {
        return "AbstractEntity <" + getUri() + "/>";
    }

}
