package cz.cvut.kbss.analysis.controller.util;

import cz.cvut.kbss.analysis.dto.error.ErrorInfo;
import cz.cvut.kbss.analysis.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorInfo handleAuthenticationError(HttpServletRequest request, Throwable t) {
        log.warn("> handleAuthenticationError - {}", request.getRequestURI());
        return ErrorInfo.builder()
                .message(t.getMessage())
                .requestUri(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorInfo handleNotFoundError(HttpServletRequest request, EntityNotFoundException e) {
        log.warn("> handleNotFoundError - {}", request.getRequestURI());
        return ErrorInfo.builder()
                .message(e.getMessage())
                .messageId(e.getMessageId())
                .requestUri(request.getRequestURI())
                .messageArguments(e.getMessageArguments())
                .build();
    }

    @ExceptionHandler(LogicViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorInfo handleLogicViolationError(HttpServletRequest request, LogicViolationException e) {
        log.warn("> handleLogicViolationError - {}", request.getRequestURI());
        return ErrorInfo.builder()
                .message(e.getMessage())
                .messageId(e.getMessageId())
                .requestUri(request.getRequestURI())
                .messageArguments(e.getMessageArguments())
                .build();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorInfo handleValidationException(HttpServletRequest request, ValidationException e) {
        log.warn("> handleValidationException - {}", request.getRequestURI());

        String errorMessage = e.getErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(",", "[", "]"));

        return ErrorInfo.builder()
                .message(errorMessage)
                .requestUri(request.getRequestURI())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorInfo handleBadRequestException(HttpServletRequest request, BadRequestException e) {
        return ErrorInfo.builder()
                .message(e.getMessage())
                .messageId(e.getMessageId())
                .requestUri(request.getRequestURI())
                .build();
    }

    @ExceptionHandler(CalculationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorInfo handleEvaluationException(HttpServletRequest request, CalculationException e) {
        log.warn("> handleEvaluationException - {}", request.getRequestURI());
        return ErrorInfo.builder()
                .message(e.getMessage())
                .requestUri(request.getRequestURI())
                .build();
    }
}
