package cz.cvut.kbss.analysis.model;


import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;


@SparqlResultSetMappings(
        @SparqlResultSetMapping(name="FaultTreeSummary", entities = {
                @EntityResult(entityClass=FaultTreeSummary.class)
        })
)
@OWLClass(iri = Vocabulary.s_c_fault_tree_summary)
@Getter
@Setter
public class FaultTreeSummary extends ManagedEntity{

    @OWLObjectProperty(iri = Vocabulary.s_p_is_artifact_of)
    protected URI systemUri;

    @OWLDataProperty(iri = Vocabulary.s_p_system_name)
    protected String systemName;

    @OWLObjectProperty(iri = Vocabulary.s_p_is_performed_by)
    protected URI subsystemUri;

    @OWLDataProperty(iri = Vocabulary.s_p_subsystem_name)
    protected String subsystemName;

    @OWLDataProperty(iri = Vocabulary.s_p_required_failure_rate)
    protected Double requiredFailureRate;

    @OWLDataProperty(iri = Vocabulary.s_p_calculated_failure_rate)
    protected Double calculatedFailureRate;

    @OWLDataProperty(iri = Vocabulary.s_p_fha_based_failure_rate)
    protected Double fhaBasedFailureRate;


    public void copyTo(FaultTree faultTree){
        super.copyTo(faultTree);
        if(this.getSystemUri() != null){
            faultTree.setSystem(new System());
            faultTree.getSystem().setUri(this.getSystemUri());
            faultTree.getSystem().setName(this.getSystemName());
        }
        if(this.getSubsystemUri() != null){
            faultTree.setSubsystem(new Item());
            faultTree.getSubsystem().setUri(this.getSubsystemUri());
            faultTree.getSubsystem().setName(this.getSubsystemName());
        }
        faultTree.setRequiredFailureRate(this.getRequiredFailureRate());
        faultTree.setCalculatedFailureRate(this.getCalculatedFailureRate());
        faultTree.setFhaBasedFailureRate(this.getFhaBasedFailureRate());
    }
}
