package cz.cvut.kbss.analysis.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class LogicViolationException extends RuntimeException {

    private final String messageId;
    private final Map<String, String> messageArguments;

    public LogicViolationException(String messageId, String message){
        super(message);
        this.messageId = messageId;
        messageArguments = null;
    }

    public LogicViolationException(String messageId, String message, Map<String, String> args){
        super(message);
        this.messageId = messageId;
        this.messageArguments = args;
    }

    public LogicViolationException(String message) {
        super(message);
        messageId = null;
        messageArguments = null;
    }

    public LogicViolationException(String message, Throwable cause) {
        super(message, cause);
        messageId = null;
        messageArguments = null;
    }

}
