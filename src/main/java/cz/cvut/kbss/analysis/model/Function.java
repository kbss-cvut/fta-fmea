package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.CascadeType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@OWLClass(iri = Vocabulary.s_c_Function)
public class Function extends AbstractEntity {

    @OWLObjectProperty(iri = Vocabulary.s_p_hasFailureMode, cascade = CascadeType.ALL)
    private Set<FailureMode> failureModes = new HashSet<>();

}
