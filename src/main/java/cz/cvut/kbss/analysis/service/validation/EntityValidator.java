package cz.cvut.kbss.analysis.service.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public interface EntityValidator extends Validator {

    void validate(Object target, Errors errors, Object... validationHints);
}
