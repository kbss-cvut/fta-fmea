package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotEmpty;
import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_Component)
@Getter
@Setter
public class Component extends NamedEntity {

    @OWLObjectProperty(iri = Vocabulary.s_p_hasFunction, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Function> functions = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_hasFailureMode, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<FailureMode> failureModes = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_isPartOf, cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private URI parentComponent;

    public void addFunction(Function function) {
        function.setComponent(this);
        getFunctions().add(function);
    }

    public void addFailureMode(FailureMode failureMode) {
        failureMode.setComponent(this);
        getFailureModes().add(failureMode);
    }

    @Override
    public String toString() {
        return "Component <" + getUri() + "/>";
    }
}
