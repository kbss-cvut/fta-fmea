package cz.cvut.kbss.analysis.exception;

public class EntityNotFoundException extends FtaFmeaException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public static EntityNotFoundException create(String resourceName, Object identifier) {
        return new EntityNotFoundException(resourceName + " identified by " + identifier + " not found.");
    }

}