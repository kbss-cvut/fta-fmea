package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.dao.BaseDao;
import cz.cvut.kbss.analysis.model.NamedEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@Qualifier("defaultEntityValidator")
@Slf4j
@Component
public class DefaultEntityValidator extends NamedEntityValidator<NamedEntity>{

    public DefaultEntityValidator(SpringValidatorAdapter validatorAdapter) {
        super(NamedEntity.class, validatorAdapter);
    }

    @Override
    protected BaseDao getPrimaryDao() {
        return null;
    }

    @Override
    protected void customValidation(NamedEntity target, Errors errors, Object... validationHints) {
        // No custom validation
    }
}
