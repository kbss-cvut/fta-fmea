package cz.cvut.kbss.analysis.controller.util;

import cz.cvut.kbss.analysis.dto.error.ErrorInfo;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorInfo handleAuthenticationError(HttpServletRequest request, Throwable t) {
        return new ErrorInfo(t.getMessage(), request.getRequestURI());
    }

}
