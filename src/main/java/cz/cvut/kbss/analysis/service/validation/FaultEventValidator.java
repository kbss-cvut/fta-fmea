package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.dao.FaultEventDao;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.fta.FtaEventType;
import cz.cvut.kbss.analysis.model.fta.GateType;
import cz.cvut.kbss.analysis.util.Vocabulary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@Qualifier("faultEventValidator")
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FaultEventValidator implements Validator {

    private final FaultEventDao faultEventDao;
    private final SpringValidatorAdapter validatorAdapter;

    @Override
    public boolean supports(Class<?> clazz) {
        return FaultEvent.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        validatorAdapter.validate(target, errors);

        FaultEvent instance = (FaultEvent) target;

        boolean duplicate = faultEventDao.existsWithPredicate(Vocabulary.s_p_hasName, instance.getName());
        if(instance.getUri() == null && duplicate) {
            errors.rejectValue("name", "name.duplicate");
        }

        if (instance.getEventType() == FtaEventType.INTERMEDIATE && (instance.getGateType() == null || instance.getGateType() == GateType.UNUSED)) {
            errors.rejectValue("gateType", "gateType.invalid");
        }

        if (instance.getEventType() != FtaEventType.INTERMEDIATE && (instance.getGateType() != null && instance.getGateType() != GateType.UNUSED)) {
            errors.rejectValue("gateType", "gateType.invalid");
        }
    }
}
