package cz.cvut.kbss.analysis.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class EntityNotFoundException extends FtaFmeaException {

    private final String messageId;
    private Map<String, String> messageArguments;

    public EntityNotFoundException(String message, String resourceName, Object identifier) {
        super(message);
        messageArguments = new HashMap<>();
        messageId = "entity_not_found";
        messageArguments.put("resourceName", resourceName);
        messageArguments.put("identifier", identifier.toString());
    }

    public static EntityNotFoundException create(String resourceName, Object identifier) {
        return new EntityNotFoundException(resourceName + " identified by " + identifier + " not found.", resourceName, identifier);
    }

}