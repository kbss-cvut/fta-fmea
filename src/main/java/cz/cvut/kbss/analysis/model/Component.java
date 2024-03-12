package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_Component)
@Getter
@Setter
public class Component extends Item {

    @OWLObjectProperty(iri = Vocabulary.s_p_isPartOf, cascade = {CascadeType.MERGE, CascadeType.REFRESH})// TODO - how to load parent components, e.g. simulate fetch EAGER
    private Item parentComponent;

    @Override
    public String toString() {
        return "Component <" + getUri() + "/>";
    }
}
