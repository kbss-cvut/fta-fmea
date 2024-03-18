package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_situation)
@Getter
@Setter
public class Situation extends AbstractEntity {

    @OWLObjectProperty(iri = Vocabulary.s_p_is_brought_about_by)
    private Set<Event> broughtAboutByEvents = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_has_trigger)
    private Set<Event> triggersEvents = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_is_activating)
    private Set<Behavior> behaviors = new HashSet<>();
}
