package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.dao.BaseDao;
import cz.cvut.kbss.analysis.dao.FaultEventDao;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.fta.FtaEventType;
import cz.cvut.kbss.analysis.model.fta.GateType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@Qualifier("faultEventValidator")
@Slf4j
@Component
public class FaultEventValidator extends NamedEntityValidator<FaultEvent> {

    private final FaultEventDao faultEventDao;

    public FaultEventValidator(SpringValidatorAdapter validatorAdapter, FaultEventDao faultEventDao) {
        super(FaultEvent.class, validatorAdapter);
        this.faultEventDao = faultEventDao;
    }

    @Override
    protected BaseDao<FaultEvent> getPrimaryDao() {
        return faultEventDao;
    }

    @Override
    protected void customValidation(FaultEvent target, Errors errors, ConstraintGroupsAdapter groups, Object... validationHints) {
        validateUri(target, errors, groups, validationHints);

        if (target.getEventType() == FtaEventType.INTERMEDIATE && (target.getGateType() == null || target.getGateType() == GateType.UNUSED)) {
            errors.rejectValue("gateType", "gateType.invalid");
        }

        if (target.getEventType() != FtaEventType.INTERMEDIATE && (target.getGateType() != null && target.getGateType() != GateType.UNUSED)) {
            errors.rejectValue("gateType", "gateType.invalid");
        }
    }
}
