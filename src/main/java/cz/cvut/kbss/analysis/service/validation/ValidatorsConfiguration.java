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

    @Bean("systemValidator")
    public NamedEntityValidator<System> systemValidator(SystemDao dao){
        return createCommonValidator(System.class, dao);
    }

    @Bean(name ="failureModeValidator")
    public NamedEntityValidator<FailureMode> failureModeValidator(FailureModeDao dao){
        return createCommonValidator(FailureMode.class, dao);
    }

    protected <T extends NamedEntity> NamedEntityValidator<T> createCommonValidator(Class<T> cls, BaseDao<T> dao){
        return new NamedEntityValidator<T>(cls, validatorAdapter) {
            private final BaseDao<T> baseDao = dao;
            @Override
            protected BaseDao<T> getPrimaryDao() {
                return baseDao;
            }
        };
    }
}
