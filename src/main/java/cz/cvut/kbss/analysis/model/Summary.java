package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.EntityResult;
import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.annotations.SparqlResultSetMapping;
import cz.cvut.kbss.jopa.model.annotations.SparqlResultSetMappings;

@SparqlResultSetMappings(
        @SparqlResultSetMapping(name="Summary", entities = {
                @EntityResult(entityClass=Summary.class)
        })
)
@OWLClass(iri = Vocabulary.s_c_summary)
public class Summary extends NamedEntity {
        public <T extends NamedEntity> T asEntity(Class<T> cls){
                T entity = null;
                try {
                        entity = cls.getConstructor().newInstance();
                }catch (Exception e){
                        throw new RuntimeException(String.format("Entity class %s does not have no-arg constructor", cls.getName()));
                }
                copyTo(entity);
                return entity;
        }

        public void copyTo(NamedEntity namedEntity){
                namedEntity.setUri(this.getUri());
                namedEntity.setName(this.getName());
                namedEntity.setDescription(this.getDescription());
        }
}
