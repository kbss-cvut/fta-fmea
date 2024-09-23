package cz.cvut.kbss.analysis.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class EntityNotFoundException extends FtaFmeaException {

    private final String messageId;
    private Map<String, String> messageArguments;

    public EntityNotFoundException(String messageId, String message, Map<String, String> args) {
        super(message);
        this.messageId = messageId;
        this.messageArguments = args;
    }

    public static EntityNotFoundException create(String resourceName, Object identifier) {
        return new EntityNotFoundException("error.entityNotFound",resourceName + " identified by " + identifier + " not found.", Map.of("resourceName", resourceName, "identifier", identifier.toString()));
    }

}