package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.exception.ValidationException;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.util.EventType;
import cz.cvut.kbss.analysis.model.util.GateType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FaultEventValidator {


    public void validate(FaultEvent faultEvent) {
        log.info("> validate");

        if (faultEvent.getEventType() == EventType.INTERMEDIATE && (faultEvent.getGateType() == null || faultEvent.getGateType() == GateType.UNUSED)) {
            String message = "INTERMEDIATE event must have gate type and it cannot be UNUSED!";
            log.warn("< validate - {}", message);
            throw new ValidationException(message);
        }

        if (faultEvent.getEventType() != EventType.INTERMEDIATE && faultEvent.getGateType() != GateType.UNUSED) {
            String message = "Non-INTERMEDIATE event must have gate type UNUSED!";
            log.warn("< validate - {}", message);
            throw new ValidationException(message);
        }

        log.info("< validate - fault event valid");
    }


}
