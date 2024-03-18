package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;

@OWLClass(iri = Vocabulary.s_c_failure_mode)
@Getter
@Setter
public class FailureMode extends Behavior {

    @OWLDataProperty(iri = Vocabulary.s_p_failure_mode_type)
    private FailureModeType failureModeType = FailureModeType.FailureMode;

    @Override
    public String toString() {
        return "FailureMode <" + getUri() + "/>";
    }
}
