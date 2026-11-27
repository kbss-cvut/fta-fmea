package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import cz.cvut.kbss.jopa.vocabulary.DC;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.util.Date;

//@SparqlResultSetMappings(
//        @SparqlResultSetMapping(name="ManagedEntitySummary", entities = {
//                @EntityResult(entityClass=ManagedEntity.class)
//        })
//)
@SparqlResultSetMappings(@SparqlResultSetMapping(name="ManagedEntitySummary",
        classes = @ConstructorResult(targetClass = ManagedEntity.class,
                variables = {
                        @VariableResult(name = "uri", type = URI.class),
                        @VariableResult(name = "name", type = String.class),
                        @VariableResult(name = "description", type = String.class),
                        @VariableResult(name = "created", type = Date.class),
                        @VariableResult(name = "modified", type = Date.class),
                        @VariableResult(name = "creator", type = URI.class),
                        @VariableResult(name = "lastEditor", type = URI.class),
                }))
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

    public ManagedEntity() {
    }

    public ManagedEntity(URI uri, String name, String description, Date created, Date modified, URI creator, URI lastEditor) {
        super(uri, name, description);
        this.created = created;
        this.modified = modified;
        this.creator = UserReference.create(creator);
        this.lastEditor = UserReference.create(lastEditor);
    }

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
