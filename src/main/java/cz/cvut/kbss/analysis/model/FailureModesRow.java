package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;

import java.net.URI;
import java.util.Objects;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_FailureModesRow)
@Data
public class FailureModesRow extends AbstractEntity {

    @ParticipationConstraints(nonEmpty = true)
    @OWLObjectProperty(iri = Vocabulary.s_p_hasLocalEffect, fetch = FetchType.EAGER)
    private URI localEffect;

    @OWLObjectProperty(iri = Vocabulary.s_p_hasEffect, fetch = FetchType.EAGER)
    private Set<URI> effects;

    // TODO add RPN

    @Override
    public String toString() {
        return "FailureModesRow <" + getUri() + "/>";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FailureModesRow that = (FailureModesRow) o;
        return Objects.equals(getUri(), that.getUri()) && Objects.equals(localEffect, that.getLocalEffect());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri());
    }

}
