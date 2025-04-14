package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.dao.FaultEventDao;
import cz.cvut.kbss.analysis.environment.Generator;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.fta.FtaEventType;
import cz.cvut.kbss.analysis.model.fta.GateType;
import cz.cvut.kbss.analysis.service.validation.groups.ValidationScopes;
import cz.cvut.kbss.analysis.util.Vocabulary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import static org.mockito.ArgumentMatchers.eq;

class FaultEventValidatorTest {

    @Mock
    FaultEventDao faultEventDao;
    @Mock
    SpringValidatorAdapter validatorAdapter;
    @InjectMocks
    FaultEventValidator faultEventValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void validateDuplicates_duplicateExists_shouldReturnError() {
        FaultEvent event = new FaultEvent();
        event.setName("Duplicate Name");

        Mockito.when(faultEventDao.existsWithPredicate(Vocabulary.s_p_name, event.getName())).thenReturn(true);

        BindingResult bindingResult = ValidationTestUtils.createBinding(event, faultEventValidator);
        faultEventValidator.validate(event, bindingResult, ValidationScopes.Create.class);
        Assertions.assertFalse(bindingResult.hasErrors());

        event.setUri(Generator.generateUri());
        Mockito.when(faultEventDao.exists(eq(event.getUri()))).thenReturn(true);

        faultEventValidator.validate(event, bindingResult, ValidationScopes.Update.class);
        Assertions.assertFalse(bindingResult.hasErrors());
    }

    @Test
    void validateDuplicates_noDuplicate_shouldJustRun() {
        FaultEvent event = new FaultEvent();
        event.setEventType(FtaEventType.BASIC);
        event.setName("Valid Name");

        Mockito.when(faultEventDao.existsWithPredicate(Vocabulary.s_p_name, event.getName())).thenReturn(false);

        BindingResult bindingResult = ValidationTestUtils.createBinding(event, faultEventValidator);
        faultEventValidator.validate(event, bindingResult);

        Assertions.assertFalse(bindingResult.hasErrors());
    }

    @Test
    void validateTypes_intermediate_nullGate_shouldReturnError() {
        FaultEvent event = new FaultEvent();
        event.setUri(Generator.generateUri());
        event.setEventType(FtaEventType.INTERMEDIATE);

        BindingResult bindingResult = ValidationTestUtils.createBinding(event, faultEventValidator);
        faultEventValidator.validate(event, bindingResult);

        Assertions.assertTrue(bindingResult.hasErrors());
        Assertions.assertNotNull(bindingResult.getFieldError("gateType"));
    }

    @Test
    void validateTypes_intermediate_unusedGate_shouldReturnError() {
        FaultEvent event = new FaultEvent();
        event.setUri(Generator.generateUri());
        event.setEventType(FtaEventType.INTERMEDIATE);
        event.setGateType(null);

        BindingResult bindingResult = ValidationTestUtils.createBinding(event, faultEventValidator);
        faultEventValidator.validate(event, bindingResult);

        Assertions.assertTrue(bindingResult.hasErrors());
        Assertions.assertNotNull(bindingResult.getFieldError("gateType"));
    }

    @Test
    void validateTypes_nonIntermediate_gateNotUnused_shouldReturnError() {
        FaultEvent event = new FaultEvent();
        event.setUri(Generator.generateUri());
        event.setEventType(FtaEventType.BASIC);
        event.setGateType(GateType.AND);

        BindingResult bindingResult = ValidationTestUtils.createBinding(event, faultEventValidator);
        faultEventValidator.validate(event, bindingResult);

        Assertions.assertTrue(bindingResult.hasErrors());
        Assertions.assertNotNull(bindingResult.getFieldError("gateType"));
    }

}