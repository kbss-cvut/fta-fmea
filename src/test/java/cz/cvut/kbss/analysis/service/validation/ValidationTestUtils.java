package cz.cvut.kbss.analysis.service.validation;

import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

public class ValidationTestUtils {

    public static BindingResult createBinding(Object instance, Validator validator) {
        DataBinder binder = new DataBinder(instance);
        binder.setValidator(validator);
        return binder.getBindingResult();
    }

}
