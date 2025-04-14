package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.model.NamedEntity;
import cz.cvut.kbss.analysis.util.Vocabulary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@Slf4j
public abstract class NamedEntityValidator<T extends NamedEntity> extends AbstractEntityValidator<T> {

    public NamedEntityValidator(Class<T> supporetedClass, SpringValidatorAdapter validatorAdapter) {
        super(supporetedClass, validatorAdapter);
    }

    protected void customValidation(T target, Errors errors, ConstraintGroupsAdapter groups, Object... validationHints ){
        super.customValidation(target, errors, groups, validationHints);
        validateName(target, errors, groups, validationHints);
    }

    protected void validateName(T target, Errors errors, ConstraintGroupsAdapter groups, Object... validationHints ){
        if(existsWithName(target))
            errors.rejectValue("name", "name.duplicate", "Duplicate entity name");
    }

    protected boolean existsWithName(NamedEntity entity){
        return existsWithName(entity.getName());
    }

    protected boolean existsWithName(String name){
        return name != null && !name.isBlank() &&
                getPrimaryDao().existsWithPredicate(Vocabulary.s_p_name, name);
    }
}
