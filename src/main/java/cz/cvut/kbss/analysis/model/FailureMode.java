package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@OWLClass(iri = Vocabulary.s_c_FailureMode)
@Getter
@Setter
public class FailureMode extends Behavior {

    @OWLDataProperty(iri = Vocabulary.s_p_hasFailureModeType)
    private FailureModeType failureModeType = FailureModeType.FailureMode;

    @Override
    public String toString() {
        return "FailureMode <" + getUri() + "/>";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FailureMode that = (FailureMode) o;
        return Objects.equals(getUri(), that.getUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri());
    }

}
