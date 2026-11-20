package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.CascadeType;
import cz.cvut.kbss.jopa.model.annotations.FetchType;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Stream;

@OWLClass(iri = Vocabulary.s_c_event)
@Getter
@Setter
public class Event extends DomainEntity<Event> {

    @OWLObjectProperty(iri = Vocabulary.s_p_is_derived_from, fetch = FetchType.EAGER)
    protected Set<Event> supertypes;

    @OWLObjectProperty(iri = Vocabulary.s_p_is_part_of, fetch = FetchType.EAGER)
    protected Set<Event> contextEvents;

    @OWLObjectProperty(iri = Vocabulary.s_p_is_manifestation_of, cascade = CascadeType.ALL)
    private Behavior behavior;

    @OWLObjectProperty(iri = Vocabulary.s_p_is_bringing_about)
    private Set<Situation> bringsAboutSituations = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_is_triggered_by)
    private Set<Situation> triggeredBySituations = new HashSet<>();

    public void setBehavior(Behavior behavior){
        Behavior lastBehavior = this.behavior;
        this.behavior = behavior;
        if(behavior == null && lastBehavior != null){
            lastBehavior.removeManifestation(this);
        }else if(behavior != null){
            behavior.addManifestation(this);
        }
    }

    public void addBringsAboutSituation(Situation situation){
        situation.getBroughtAboutByEvents().add(this);
        getBringsAboutSituations().add(situation);
    }

    public void addTriggeredBySituation(Situation situation){
        situation.getTriggersEvents().add(this);
        getTriggeredBySituations().add(situation);
    }

    /**
     *
     * @return all items associated trough behavior to any of the events in the closure of the supertypes property.
     */
    public List<Item> getAllRelatedItems(){
        List<Item> ret = new ArrayList<>();
        for(Event type : getRelatedEventTypes()){
            if(type.getBehavior() != null){
                ret.add(type.getBehavior().getItem());
            }
        }
        return ret;
    }


    /**
     *
     * @return the result of {@code Event#_getRelatedEventTypes(this)}
     * @see _getRelatedEventTypes
     */
    public Collection<Event> getRelatedEventTypes(){
        return _getRelatedEventTypes(this);
    }

    /**
     * Returns all the event types in the closure of the supertype field. Also includes the input Event if it is an instance of
     * FaultEventType.
     * @param evt
     * @return
     */
    public static Collection<Event> _getRelatedEventTypes(Event evt){
        Set<Event> eventTypes = new HashSet<>();

        if(evt.getSupertypes() == null || evt.getSupertypes().isEmpty())
            return eventTypes;
        Stack<Event> stack = new Stack<>();
        if(evt instanceof FaultEventType)
            stack.push(evt);
        else
            evt.getSupertypes().forEach(stack::push);

        while (!stack.isEmpty()) {
            Event e = stack.pop();
            eventTypes.add(e);
            if(e.getSupertypes() != null)
                e.getSupertypes().forEach(stack::push);
        }
        return eventTypes;
    }
}
