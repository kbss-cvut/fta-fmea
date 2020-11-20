package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.dao.FaultEventDao;
import cz.cvut.kbss.analysis.exception.ValidationException;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.util.EventType;
import cz.cvut.kbss.analysis.model.util.GateType;
import cz.cvut.kbss.analysis.util.Vocabulary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FaultEventValidator {

    private final FaultEventDao faultEventDao;

    public void validateDuplicates(FaultEvent faultEvent) {
        log.info("> validateDuplicates");

        boolean duplicate = faultEventDao.existsWithPredicate(Vocabulary.s_p_hasName, faultEvent.getName());
        if(duplicate) {
            String message = "Fault event has duplicate name!!";
            log.warn("< validateDuplicates - {}", message);
            throw new ValidationException(message);
        }

        log.info("< validateDuplicates - fault event unique");
    }

    public void validateTypes(FaultEvent faultEvent) {
        log.info("> validateTypes");

        if (faultEvent.getEventType() == EventType.INTERMEDIATE && (faultEvent.getGateType() == null || faultEvent.getGateType() == GateType.UNUSED)) {
            String message = "INTERMEDIATE event must have gate type and it cannot be UNUSED!";
            log.warn("< validateTypes - {}", message);
            throw new ValidationException(message);
        }

        if (faultEvent.getEventType() != EventType.INTERMEDIATE && faultEvent.getGateType() != GateType.UNUSED) {
            String message = "Non-INTERMEDIATE event must have gate type UNUSED!";
            log.warn("< validateTypes - {}", message);
            throw new ValidationException(message);
        }

        log.info("< validateTypes - fault event valid");
    }


}
