package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.dao.FunctionDao;
import cz.cvut.kbss.analysis.model.Function;
import cz.cvut.kbss.analysis.util.Vocabulary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@Qualifier("functionValidator")
@Slf4j
@org.springframework.stereotype.Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FunctionValidator implements Validator {

    private final FunctionDao functionDao;
    private final SpringValidatorAdapter validatorAdapter;

    @Override
    public boolean supports(Class<?> clazz) {
        return Function.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        validatorAdapter.validate(target, errors);

        Function instance = (Function) target;

        boolean duplicate = functionDao.existsWithPredicate(Vocabulary.s_p_name, instance.getName());
        if(instance.getUri() == null && duplicate) {
            errors.rejectValue("name", "name.duplicate", "Duplicate component name");
        }
    }

}
