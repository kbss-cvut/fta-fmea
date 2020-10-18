package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.model.util.HasIdentifier;
import cz.cvut.kbss.analysis.model.util.HasTypes;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;

import java.io.Serializable;
import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@MappedSuperclass
@Data
public abstract class Event implements HasIdentifier, HasTypes, Serializable {

    @Id(generated = true)
    private URI uri;

    // TODO are types necessary?
    @Types
    protected Set<String> types;

    @Override
    public String toString() {
        return "Event <" + getUri() + "/>";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event that = (Event) o;
        return Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

}
