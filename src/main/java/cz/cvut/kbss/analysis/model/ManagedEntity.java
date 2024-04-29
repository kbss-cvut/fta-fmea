package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import cz.cvut.kbss.jopa.vocabulary.DC;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@SparqlResultSetMappings(
        @SparqlResultSetMapping(name="ManagedEntitySummary", entities = {
                @EntityResult(entityClass=ManagedEntity.class)
        })
)
@OWLClass(iri = Vocabulary.s_c_managed_entity)
@Getter
@Setter
public class ManagedEntity extends NamedEntity{

    @OWLDataProperty(iri = DC.Terms.CREATED)
    protected Date created;

    @OWLDataProperty(iri = DC.Terms.MODIFIED)
    protected Date modified;

    @OWLObjectProperty(iri = DC.Terms.CREATOR)
    protected UserReference creator;

    @OWLObjectProperty(iri = Vocabulary.s_c_editor)
    protected UserReference lastEditor;

    @Override
    public void setAs(NamedEntity namedEntity) {
        ((ManagedEntity)namedEntity).copyTo(this);
    }

    public void copyTo(ManagedEntity managedEntity) {
        super.copyTo(managedEntity);
        managedEntity.setCreated(this.created);
        managedEntity.setModified(this.modified);
        managedEntity.setCreator(this.creator);
        managedEntity.setLastEditor(this.lastEditor);
    }
}
