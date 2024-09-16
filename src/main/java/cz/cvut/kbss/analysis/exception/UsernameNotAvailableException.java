package cz.cvut.kbss.analysis.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class UsernameNotAvailableException extends RuntimeException {

    private final String messageId;
    private Map<String, String> messageArguments;

    public UsernameNotAvailableException(String message) {
        super(message);
        messageId = null;
    }

    public UsernameNotAvailableException(String messageId, String message, Map<String, String> args){
        super(message);
        this.messageId = messageId;
        messageArguments = args;
    }

}
