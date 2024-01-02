package cz.cvut.kbss.analysis.exception;


/**
 * Application-specific exception.
 * <p>
 * All exceptions related to the application should be subclasses of this one.
 */
public class FtaFmeaException extends RuntimeException {

    protected FtaFmeaException() {
    }

    public FtaFmeaException(String message) {
        super(message);
    }

    public FtaFmeaException(String message, Throwable cause) {
        super(message, cause);
    }

    public FtaFmeaException(Throwable cause) {
        super(cause);
    }
}
