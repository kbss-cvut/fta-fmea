package cz.cvut.kbss.analysis.controller.util;

import cz.cvut.kbss.analysis.dto.error.ErrorInfo;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.exception.LogicViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorInfo handleAuthenticationError(HttpServletRequest request, Throwable t) {
        log.warn("> handleAuthenticationError - {}", request.getRequestURI());
        return new ErrorInfo(t.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorInfo handleNotFoundError(HttpServletRequest request, Throwable t) {
        log.warn("> handleNotFoundError - {}", request.getRequestURI());
        return new ErrorInfo(t.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(LogicViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorInfo handleLogicViolationError(HttpServletRequest request, Throwable t) {
        log.warn("> handleLogicViolationError - {}", request.getRequestURI());
        return new ErrorInfo(t.getMessage(), request.getRequestURI());
    }

}
