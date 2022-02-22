package cz.cvut.kbss.analysis.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.ObjectError;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@RequiredArgsConstructor
public class ValidationException extends RuntimeException {

    private final List<ObjectError> errors;

}
