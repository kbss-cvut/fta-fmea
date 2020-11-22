package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.persistence.util.HasAuthorDataManager;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;

import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_FailureMode)
@EntityListeners(HasAuthorDataManager.class)
@Data
public class FailureMode extends HasAuthorData {

    @OWLDataProperty(iri = Vocabulary.s_p_hasName)
    private String name;

    @OWLObjectProperty(iri = Vocabulary.s_p_hasEffect, cascade = {CascadeType.REFRESH, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private Set<FaultEvent> effects = new HashSet<>();

    @OWLObjectProperty(iri = Vocabulary.s_p_influences, cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private Function function;

    @OWLObjectProperty(iri = Vocabulary.s_p_isMitigatedBy, cascade = CascadeType.ALL)
    private Set<Mitigation> mitigation = new HashSet<>();

    public void addMitigation(Mitigation mitigation) {
        getMitigation().add(mitigation);
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
