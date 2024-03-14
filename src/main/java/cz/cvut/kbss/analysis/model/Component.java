package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.CascadeType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import lombok.Getter;
import lombok.Setter;

@OWLClass(iri = Vocabulary.s_c_component)
@Getter
@Setter
public class Component extends Item {

    @OWLObjectProperty(iri = Vocabulary.s_p_is_part_of, cascade = {CascadeType.MERGE, CascadeType.REFRESH})// TODO - how to load parent components, e.g. simulate fetch EAGER
    private Item parentComponent;

    @Override
    public String toString() {
        return "Component <" + getUri() + "/>";
    }
}
