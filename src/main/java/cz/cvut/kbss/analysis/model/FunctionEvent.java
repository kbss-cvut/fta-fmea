package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_function_event)
@Getter
@Setter
public class FunctionEvent extends Event{

    @OWLObjectProperty(iri = Vocabulary.s_p_is_manifestation_of)
    private Set<Function> functions = new HashSet<>();

    public void addFunction(Function function){
        function.getManifestations().add(this);
        getFunctions().add(function);
    }
}
