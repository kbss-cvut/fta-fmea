package cz.cvut.kbss.analysis.model;


import cz.cvut.kbss.analysis.model.ava.ATASystem;
import cz.cvut.kbss.analysis.model.ava.FHAEventType;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;


@SparqlResultSetMappings(
        @SparqlResultSetMapping(name="FaultTreeSummary", entities = {
                @EntityResult(entityClass=FaultTreeSummary.class)
        })
)
@OWLClass(iri = Vocabulary.s_c_fault_tree_summary)
@Getter
@Setter
public class FaultTreeSummary extends ManagedEntity{

    @OWLDataProperty(iri = Vocabulary.s_p_status)
    protected String status;

    @OWLObjectProperty(iri = Vocabulary.s_p_is_derived_from)
    protected URI rootEvent;

    @OWLObjectProperty(iri = Vocabulary.s_p_is_derived_from)
    protected URI rootEventType;

    @OWLObjectProperty(iri = Vocabulary.s_p_is_artifact_of)
    protected URI systemUri;

    @OWLDataProperty(iri = Vocabulary.s_p_system_name)
    protected String systemName;

    @OWLObjectProperty(iri = Vocabulary.s_p_is_performed_by)
    protected URI subsystemUri;

    @OWLDataProperty(iri = Vocabulary.s_p_subsystem_name)
    protected String subsystemName;

    @OWLDataProperty(iri = Vocabulary.s_p_ata_code, simpleLiteral = true)
    private String ataCode;

    @OWLDataProperty(iri = Vocabulary.s_p_alt_name)
    protected String subsystemAltNames;

    @OWLDataProperty(iri = Vocabulary.s_p_required_failure_rate)
    protected Double requiredFailureRate;

    @OWLDataProperty(iri = Vocabulary.s_p_calculated_failure_rate)
    protected Double calculatedFailureRate;

    @OWLDataProperty(iri = Vocabulary.s_p_fha_based_failure_rate)
    protected Double fhaBasedFailureRate;

    @OWLDataProperty(iri = Vocabulary.s_p_auxiliary)
    protected Boolean auxiliary;

    public void copyTo(FaultTree faultTree){
        super.copyTo(faultTree);
        if(this.getRootEvent() != null){
            FaultEvent root = new FaultEvent();
            root.setUri(this.getRootEvent());
            faultTree.setManifestingEvent(root);
            if(this.getRootEventType() != null){
                FHAEventType rootType = new FHAEventType();
                rootType.setUri(this.getRootEventType());
                root.setSupertypes(new HashSet<>());
                root.getSupertypes().add(rootType);
                rootType.setAuxiliary(auxiliary);
            }
        }

        if(this.getSystemUri() != null){
            faultTree.setSystem(new System());
            faultTree.getSystem().setUri(this.getSystemUri());
            faultTree.getSystem().setName(this.getSystemName());
        }

        if(this.getSubsystemUri() != null){
            ATASystem subsystem = new ATASystem();
            subsystem.setUri(this.getSubsystemUri());
            subsystem.setName(this.getSubsystemName());
            subsystem.setAtaCode(this.getAtaCode());
            subsystem.setAltNames(Optional.ofNullable(this.getSubsystemAltNames())
                    .filter(ns -> !ns.isBlank())
                    .map(ns -> new HashSet<>(Arrays.asList(ns.split("\\n"))))
                    .orElse(null)
            );
            faultTree.setSubsystem(subsystem);
        }

        Optional.ofNullable(this.getStatus()).map(Status::valueOf).ifPresent(faultTree::setStatus);
        faultTree.setRequiredFailureRate(this.getRequiredFailureRate());
        faultTree.setCalculatedFailureRate(this.getCalculatedFailureRate());
        faultTree.setFhaBasedFailureRate(this.getFhaBasedFailureRate());
    }
}
