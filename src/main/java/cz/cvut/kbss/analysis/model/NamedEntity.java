package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

import static cz.cvut.kbss.analysis.service.validation.groups.ValidationScopes.*;

@SparqlResultSetMappings(
        @SparqlResultSetMapping(name="Summary", entities = {
                @EntityResult(entityClass=NamedEntity.class)
        })
)
@OWLClass(iri = Vocabulary.s_c_named_entity)
@Getter
@Setter
public class NamedEntity extends AbstractEntity{

    @NotEmpty(message = "Name must not be empty", groups = {Create.class, Update.class})
    @ParticipationConstraints(nonEmpty = true)
    @OWLDataProperty(iri = Vocabulary.s_p_name)
    private String name;

    @OWLDataProperty(iri = Vocabulary.s_p_alt_name)
    private Set<String> altNames;

    @OWLDataProperty(iri = Vocabulary.s_p_description)
    private String description;


    public <T extends NamedEntity> T asEntity(Class<T> cls){
        T entity = newInstance(cls);
        entity.setAs(this);
        return entity;
    }

    public void setAs(NamedEntity namedEntity){
        namedEntity.copyTo(this);
    }

    public void copyTo(NamedEntity namedEntity){
        namedEntity.setUri(this.getUri());
        namedEntity.setName(this.getName());
        namedEntity.setDescription(this.getDescription());
    }

    public static <T extends AbstractEntity> T newInstance(Class<T> cls){
        try {
            return cls.getConstructor().newInstance();
        }catch (Exception e){
            throw new RuntimeException(String.format("Entity class %s does not have no-arg constructor", cls.getName()));
        }
    }

}
