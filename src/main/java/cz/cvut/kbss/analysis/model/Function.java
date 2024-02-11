package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@OWLClass(iri = Vocabulary.s_c_Function)
@Getter
@Setter
public class Function extends Behavior {

    @Override
    public String toString() {
        return "Function <" + getUri() + "/>";
    }


}

