package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.dao.*;
import cz.cvut.kbss.analysis.model.System;
import cz.cvut.kbss.analysis.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ValidatorsConfiguration {

    private final SpringValidatorAdapter validatorAdapter;

    @Bean
    public EntityValidator systemValidator(SystemDao dao){
        return namedEntityValidator(dao);
    }

    @Bean
    public EntityValidator failureModeValidator(FailureModeDao dao){
        return namedEntityValidator(dao);
    }

    @Bean
    public EntityValidator faultTreeValidator(FaultTreeDao dao){
        return namedEntityValidator(dao);
    }

    @Bean
    public EntityValidator functionValidator(FunctionDao dao){
        return namedEntityValidator(dao);
    }

    @Bean
    public EntityValidator componentValidator(ComponentDao dao){
        return namedEntityValidator(dao);
    }

    @Bean
    public EntityValidator userValidator(FaultTreeDao dao){
        return abstractEntityValidator(dao);
    }





    protected <T extends NamedEntity> NamedEntityValidator<T> namedEntityValidator(BaseDao<T> dao){
        return namedEntityValidator(dao, validatorAdapter);
    }

    public static <T extends NamedEntity> NamedEntityValidator<T> namedEntityValidator(BaseDao<T> dao, SpringValidatorAdapter validatorAdapter){
        return new NamedEntityValidator<T>(dao.getType(), validatorAdapter) {
            private final BaseDao<T> baseDao = dao;
            @Override
            protected BaseDao<T> getPrimaryDao() {
                return baseDao;
            }
        };
    }

    protected <T extends AbstractEntity> AbstractEntityValidator<T> abstractEntityValidator(BaseDao<T> dao){
        return abstractEntityValidator(dao, validatorAdapter);
    }

    public static <T extends AbstractEntity> AbstractEntityValidator<T> abstractEntityValidator(BaseDao<T> dao, SpringValidatorAdapter validatorAdapter){
        return new AbstractEntityValidator<T>(dao.getType(), validatorAdapter) {
            private final BaseDao<T> baseDao = dao;
            @Override
            protected BaseDao<T> getPrimaryDao() {
                return baseDao;
            }
        };
    }
}
