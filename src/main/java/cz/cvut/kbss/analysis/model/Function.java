package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import lombok.Getter;
import lombok.Setter;

@OWLClass(iri = Vocabulary.s_c_function)
@Getter
@Setter
public class Function extends Behavior {

    @Override
    public String toString() {
        return "Function <" + getUri() + "/>";
    }


}

