package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.dao.FaultEventDao;
import cz.cvut.kbss.analysis.environment.Generator;
import cz.cvut.kbss.analysis.exception.ValidationException;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.util.EventType;
import cz.cvut.kbss.analysis.model.util.GateType;
import cz.cvut.kbss.analysis.util.Vocabulary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FaultEventValidatorTest {

    @Mock
    FaultEventDao faultEventDao;

    @InjectMocks
    FaultEventValidator faultEventValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void validateDuplicates_duplicateExists_shouldThrowException() {
        FaultEvent event = new FaultEvent();
        event.setName("Duplicate Name");
        event.setUri(Generator.generateUri());

        Mockito.when(faultEventDao.existsWithPredicate(Vocabulary.s_p_hasName, event.getName())).thenReturn(true);

        assertThrows(ValidationException.class, () -> faultEventValidator.validateDuplicates(event));
    }

    @Test
    void validateDuplicates_noDuplicate_shouldJustRun() {
        FaultEvent event = new FaultEvent();
        event.setName("Valid Name");
        event.setUri(Generator.generateUri());

        Mockito.when(faultEventDao.existsWithPredicate(Vocabulary.s_p_hasName, event.getName())).thenReturn(false);

        faultEventValidator.validateDuplicates(event);
    }

    @Test
    void validateTypes_intermediate_nullGate_shouldThrowException() {
        FaultEvent event = new FaultEvent();
        event.setUri(Generator.generateUri());
        event.setEventType(EventType.INTERMEDIATE);

        assertThrows(ValidationException.class, () -> faultEventValidator.validateTypes(event));
    }

    @Test
    void validateTypes_intermediate_unusedGate_shouldThrowException() {
        FaultEvent event = new FaultEvent();
        event.setUri(Generator.generateUri());
        event.setEventType(EventType.INTERMEDIATE);
        event.setGateType(GateType.UNUSED);

        assertThrows(ValidationException.class, () -> faultEventValidator.validateTypes(event));
    }

    @Test
    void validateTypes_nonIntermediate_gateNotUnused_shouldThrowException() {
        FaultEvent event = new FaultEvent();
        event.setUri(Generator.generateUri());
        event.setEventType(EventType.BASIC);
        event.setGateType(GateType.AND);

        assertThrows(ValidationException.class, () -> faultEventValidator.validateTypes(event));
    }

}