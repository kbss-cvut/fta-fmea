package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.dao.BaseDao;
import cz.cvut.kbss.analysis.dao.FaultTreeDao;
import cz.cvut.kbss.analysis.model.FaultTree;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@Qualifier("faultTreeValidator")
@Slf4j
@Component
public class FaultTreeValidator extends NamedEntityValidator<FaultTree> {

    private final FaultTreeDao faultTreeDao;

    public FaultTreeValidator(SpringValidatorAdapter validatorAdapter, FaultTreeDao faultTreeDao) {
        super(FaultTree.class, validatorAdapter);
        this.faultTreeDao = faultTreeDao;
    }

    @Override
    protected BaseDao<FaultTree> getPrimaryDao() {
        return faultTreeDao;
    }
}
