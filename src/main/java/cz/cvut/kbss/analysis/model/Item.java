package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_item)
@Getter
@Setter
public class Item extends DomainEntity<Item> {

    @OWLObjectProperty(iri = Vocabulary.s_p_is_derived_from, fetch = FetchType.EAGER)
    protected Set<Item> supertypes;

    @OWLObjectProperty(iri = Vocabulary.s_p_is_documented_in)
    private Set<Document> documents;

    @OWLObjectProperty(iri = Vocabulary.s_p_has_part_component, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Component> components = new HashSet<>();

    public void addComponent(Component component) {
        getComponents().add(component);
        component.setParentComponent(this);
    }

    @OWLObjectProperty(iri = Vocabulary.s_p_has_function, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Function> functions = new HashSet<>();
    @OWLObjectProperty(iri = Vocabulary.s_p_has_failure_mode, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<FailureMode> failureModes = new HashSet<>();


    public void addFunction(Function function) {
        function.setItem(this);
        getFunctions().add(function);
    }

    public void addFailureMode(FailureMode failureMode) {
        failureMode.setItem(this);
        getFailureModes().add(failureMode);
    }
}
