package cz.cvut.kbss.analysis.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Generic exception for bad requests.
 */
@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    private final String messageId;

    public BadRequestException(String messageId, String message){
        super(message);
        this.messageId = messageId;
    }

    public BadRequestException(String message) {
        super(message);
        this.messageId = null;
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
        this.messageId = null;
    }
}
