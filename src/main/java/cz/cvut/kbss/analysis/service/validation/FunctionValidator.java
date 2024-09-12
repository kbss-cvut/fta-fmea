package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.dao.BaseDao;
import cz.cvut.kbss.analysis.dao.FunctionDao;
import cz.cvut.kbss.analysis.model.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@Qualifier("functionValidator")
@Slf4j
@Component
public class FunctionValidator extends NamedEntityValidator<Function> {

    private final FunctionDao functionDao;

    public FunctionValidator(SpringValidatorAdapter validatorAdapter, FunctionDao functionDao) {
        super(Function.class, validatorAdapter);
        this.functionDao = functionDao;
    }

    @Override
    protected BaseDao<Function> getPrimaryDao() {
        return functionDao;
    }

}
