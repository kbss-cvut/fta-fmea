package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import cz.cvut.kbss.jsonld.annotation.JsonLdProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@OWLClass(iri = Vocabulary.s_c_TreeNode)
@Data
@NoArgsConstructor
public class TreeNode extends AbstractEntity {

    public TreeNode(FaultEvent event) {
        this.event = event;
    }

    @JsonLdProperty(access = JsonLdProperty.Access.WRITE_ONLY)
    @OWLObjectProperty(iri = Vocabulary.s_p_hasParent)
    private TreeNode parent;

    @OWLObjectProperty(iri = Vocabulary.s_p_hasChildren, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<TreeNode> children = new HashSet<>();

    @ParticipationConstraints(nonEmpty = true)
    @OWLObjectProperty(iri = Vocabulary.s_p_holds, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private FaultEvent event;

    public void addChild(TreeNode child) {
        getChildren().add(child);
        child.setParent(this);
    }

    @Override
    public String toString() {
        return "TreeNode <" + getUri() + "/>";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TreeNode that = (TreeNode) o;
        return Objects.equals(getUri(), that.getUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUri());
    }

}

