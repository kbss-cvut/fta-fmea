package cz.cvut.kbss.analysis.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class BadCredentialsException extends org.springframework.security.authentication.BadCredentialsException {

    private final String messageId;
    private Map<String, String> messageArguments;

    public BadCredentialsException(String message) {
        super(message);
        this.messageId = null;
    }

    public BadCredentialsException(String message, Throwable cause) {
        super(message, cause);
        this.messageId = null;
    }

    public BadCredentialsException(String messageId, String message, Map<String, String> args){
        super(message);
        this.messageId = messageId;
        messageArguments = args;
    }
}