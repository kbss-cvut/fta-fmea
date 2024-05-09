package cz.cvut.kbss.analysis.model.ava;

import cz.cvut.kbss.analysis.model.FaultEventType;
import cz.cvut.kbss.analysis.model.method.VerificationMethod;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.OWLDataProperty;
import cz.cvut.kbss.jopa.model.annotations.OWLObjectProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_fha_fault_event)
@Setter
@Getter
public class FHAEventType extends FaultEventType {

    @OWLDataProperty(iri = Vocabulary.s_p_criticality)
    private Set<Integer> criticality;

    @OWLObjectProperty(iri = Vocabulary.s_c_verification_method)
    private Set<VerificationMethod> verificationMethods;

    @OWLDataProperty(iri = Vocabulary.s_p_material_reference)
    private Set<String> referencedMaterials;
}
