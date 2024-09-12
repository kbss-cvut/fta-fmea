package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.dao.BaseDao;
import cz.cvut.kbss.analysis.dao.ComponentDao;
import cz.cvut.kbss.analysis.model.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@Qualifier("componentValidator")
@Slf4j
@org.springframework.stereotype.Component
public class ComponentValidator extends NamedEntityValidator<Component> {

    private final ComponentDao componentDao;

    public ComponentValidator(SpringValidatorAdapter validatorAdapter, ComponentDao componentDao) {
        super(Component.class, validatorAdapter);
        this.componentDao = componentDao;
    }

    @Override
    protected BaseDao<Component> getPrimaryDao() {
        return componentDao;
    }
}
