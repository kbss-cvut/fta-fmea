package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.model.util.AbstractEntity;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@OWLClass(iri = Vocabulary.FailureMode)
public class FailureMode extends AbstractEntity {

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.p_has_name)
    private String name;

}
