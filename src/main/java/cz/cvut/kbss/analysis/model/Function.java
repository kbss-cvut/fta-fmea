package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_Function)
@Getter
@Setter
public class Function extends Behavior {

    @NotEmpty(message = "Name must not be empty")
    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasName)
    private String name;

    @OWLObjectProperty(iri = Vocabulary.s_p_requires, fetch = FetchType.EAGER)
    private Set<Function> requiredFunctions = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_manifestedBy)
    private Set<FunctionEvent> manifestedByEvents = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_impairedBy)
    private Set<FailureMode> failureModes = new HashSet<>();

    public void addFunction(Function function){ getRequiredFunctions().add(function); }

    @Override
    public String toString() {
        return "Function <" + getUri() + "/>";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Function that = (Function) o;
        return Objects.equals(getUri(), that.getUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri());
    }

}

