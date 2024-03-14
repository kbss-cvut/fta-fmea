package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.ParticipationConstraints;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import java.util.Objects;

@OWLClass(iri = Vocabulary.s_c_mitigation)
@Getter
@Setter
public class Mitigation extends Behavior {

    @Override
    public String toString() {
        return "Mitigation <" + getUri() + "/>";
    }
}
