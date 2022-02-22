package cz.cvut.kbss.analysis.model.util;

import java.net.URI;

/**
 * Interface for all identifiable entity classes.
 *
 * <p>Specifies getter and setter for instance identifier, so that generic entity-processing
 * routines can be written.
 */
public interface HasIdentifier {

    URI getUri();

    void setUri(URI uri);

}