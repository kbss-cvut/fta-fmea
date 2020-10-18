package cz.cvut.kbss.analysis.exception;

public class LogicViolationException extends RuntimeException {
    public LogicViolationException() { }

    public LogicViolationException(String message) {
        super(message);
    }

    public LogicViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}
