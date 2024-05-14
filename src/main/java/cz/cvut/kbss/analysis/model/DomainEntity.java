package cz.cvut.kbss.analysis.model;

import cz.cvut.kbss.analysis.model.util.TypeCategory;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.annotations.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@MappedSuperclass
@Getter
@Setter
public abstract class DomainEntity<T extends DomainEntity> extends ManagedEntity{

    @OWLDataProperty(iri = Vocabulary.s_p_has_type_category, simpleLiteral = true)
    private TypeCategory typeCategory;

    public abstract Set<T> getSupertypes();
    public abstract void setSupertypes(Set<T> supertypes);

}
