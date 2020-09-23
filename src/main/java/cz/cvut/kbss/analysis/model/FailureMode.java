package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.net.URI;

@Getter
@Setter
@ToString
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "FailureMode.findAll",
                query = "SELECT ?x WHERE { ?x a <" + Vocabulary.FailureMode + "> . }"
        )
})
@OWLClass(iri = Vocabulary.FailureMode)
public class FailureMode implements Serializable {

    @Id(generated = true)
    private URI uri;

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.p_name)
    private String name;

}
