package cz.cvut.kbss.analysis.model.util;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Implemented by entities with {@link cz.cvut.kbss.jopa.model.annotations.Types} attribute.
 *
 * <p>Adds default implementation for adding and removing types.
 */
public interface HasTypes {

    Set<String> getTypes();

    void setTypes(Set<String> types);

    /**
     * Adds the specified type to this instance's types.
     *
     * @param type Type to add
     */
    default void addType(String type) {
        Objects.requireNonNull(type);
        if (getTypes() == null) {
            setTypes(new HashSet<>(2));
        }
        getTypes().add(type);
    }

    /**
     * Removes the specified type from this instance's types.
     *
     * <p>If the type is not present, this is a no-op.
     *
     * @param type Type to remove
     */
    default void removeType(String type) {
        Objects.requireNonNull(type);
        if (getTypes() == null) {
            return;
        }
        getTypes().remove(type);
    }
}
