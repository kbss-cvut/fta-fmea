package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.persistence.util.HasAuthorDataManager;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Data;

import java.util.Objects;

@OWLClass(iri = Vocabulary.s_c_FaultTree)
@EntityListeners(HasAuthorDataManager.class)
@Data
public class FaultTree extends HasAuthorData {

    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_hasName)
    private String name;

    @ParticipationConstraints(nonEmpty = true)
    @OWLObjectProperty(iri = Vocabulary.s_p_isManifestedBy, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private TreeNode manifestingNode;

    @Override
    public String toString() {
        return "FaultTree <" + getUri() + "/>";
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
