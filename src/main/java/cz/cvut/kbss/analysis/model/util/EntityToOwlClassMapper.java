package cz.cvut.kbss.analysis.model.util;

import cz.cvut.kbss.jopa.model.annotations.OWLClass;

/**
 * Utility class for getting information about the entity - OWL class mapping.
 */
public final class EntityToOwlClassMapper {

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
}
