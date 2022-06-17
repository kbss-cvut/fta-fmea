package cz.cvut.kbss.analysis.model.util;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;
import cz.cvut.kbss.jopa.model.metamodel.EntityType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for getting information about the entity - OWL class mapping.
 */
public final class EntityToOwlClassMapper {

    private static final Map<String, Class> iri2class = new HashMap<>();

    public static void init(Collection<EntityType<?>> types){
        for( EntityType type: types){
            Class cls = type.getJavaType();
            String iri = getOwlClassForEntity(cls);
            iri2class.put(iri, cls);
        }
    }

    /**
     * Gets IRI of the OWL class mapped by the specified entity.
     *
     * @param entityClass Entity class
     * @return IRI of mapped OWL class (as String)
     */
    public static String getOwlClassForEntity(Class<?> entityClass) {
        final OWLClass owlClass = entityClass.getDeclaredAnnotation(OWLClass.class);
        if (owlClass == null) {
            throw new IllegalArgumentException("Class " + entityClass + " is not an OWL entity.");
        }
        return owlClass.iri();
    }

    public static Class getEntityClassForIRI(String iri){
        return iri2class.get(iri);
    }

}
