package cz.cvut.kbss.analysis.exception;

import lombok.Getter;

@Getter
public class LogicViolationException extends RuntimeException {

    private final String messageId;

    public LogicViolationException(String messageId, String message){
        super(message);
        this.messageId = messageId;
    }

    public LogicViolationException(String message) {
        super(message);
        messageId = null;
    }

    public LogicViolationException(String message, Throwable cause) {
        super(message, cause);
        messageId = null;
    }

}
