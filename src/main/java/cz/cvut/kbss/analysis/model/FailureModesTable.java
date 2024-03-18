package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.CascadeType;
import cz.cvut.kbss.jopa.model.annotations.FetchType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_failure_modes_table)
@Getter
@Setter
public class FailureModesTable extends NamedEntity {


    @OWLObjectProperty(iri = Vocabulary.s_p_is_derived_from, fetch = FetchType.LAZY)
    private FaultTree faultTree;

    @OWLObjectProperty(iri = Vocabulary.s_p_has_row, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<FailureModesRow> rows = new HashSet<>();

    @Override
    public String toString() {
        return "FailureModesTable <" + getUri() + "/>";
    }
}
