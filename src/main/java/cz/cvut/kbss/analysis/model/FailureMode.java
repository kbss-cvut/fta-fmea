package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_FailureMode)
@Getter
@Setter
public class FailureMode extends Behavior {

    @NotEmpty(message = "Name must not be empty")
    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasName)
    private String name;

    @OWLObjectProperty(iri = Vocabulary.s_p_hasEffect)
    private Set<FaultEvent> effects = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_hasComponent, fetch = FetchType.EAGER)
    private Component component;

    @OWLObjectProperty(iri = Vocabulary.s_p_impairs, fetch = FetchType.EAGER)
    private Set<Function> functions;

    @OWLObjectProperty(iri = Vocabulary.s_p_isMitigatedBy, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Mitigation mitigation;

    public void addEffect(FaultEvent faultEvent) {
        faultEvent.setFailureMode(this);
        getEffects().add(faultEvent);
    }

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
