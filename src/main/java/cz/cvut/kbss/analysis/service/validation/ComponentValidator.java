package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.dao.ComponentDao;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.util.Vocabulary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@Qualifier("componentValidator")
@Slf4j
@org.springframework.stereotype.Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ComponentValidator implements Validator {

    private final ComponentDao componentDao;
    private final SpringValidatorAdapter validatorAdapter;

    @Override
    public boolean supports(Class<?> clazz) {
        return Component.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        validatorAdapter.validate(target, errors);

        Component instance = (Component) target;

        boolean duplicate = componentDao.existsWithPredicate(Vocabulary.s_p_hasName, instance.getName());
        if(instance.getUri() == null && duplicate) {
            errors.rejectValue("name", "name.duplicate", "Duplicate component name");
        }
    }

}
