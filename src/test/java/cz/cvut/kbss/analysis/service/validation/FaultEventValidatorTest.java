package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.dao.FaultEventDao;
import cz.cvut.kbss.analysis.environment.Generator;
import cz.cvut.kbss.analysis.exception.ValidationException;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.util.EventType;
import cz.cvut.kbss.analysis.model.util.GateType;
import cz.cvut.kbss.analysis.util.Vocabulary;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.xml.validation.Validator;

import static org.junit.jupiter.api.Assertions.assertThrows;

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

        Mockito.when(faultEventDao.existsWithPredicate(Vocabulary.s_p_hasName, event.getName())).thenReturn(true);

        BindingResult bindingResult = ValidationTestUtils.createBinding(event, faultEventValidator);
        faultEventValidator.validate(event, bindingResult);

        Assert.assertTrue(bindingResult.hasErrors());
        Assert.assertNotNull(bindingResult.getFieldError("name"));
    }

    @Test
    void validateDuplicates_noDuplicate_shouldJustRun() {
        FaultEvent event = new FaultEvent();
        event.setEventType(EventType.BASIC);
        event.setGateType(GateType.UNUSED);
        event.setName("Valid Name");

        Mockito.when(faultEventDao.existsWithPredicate(Vocabulary.s_p_hasName, event.getName())).thenReturn(false);

        BindingResult bindingResult = ValidationTestUtils.createBinding(event, faultEventValidator);
        faultEventValidator.validate(event, bindingResult);

        Assert.assertFalse(bindingResult.hasErrors());
    }

    @Test
    void validateTypes_intermediate_nullGate_shouldReturnError() {
        FaultEvent event = new FaultEvent();
        event.setUri(Generator.generateUri());
        event.setEventType(EventType.INTERMEDIATE);

        BindingResult bindingResult = ValidationTestUtils.createBinding(event, faultEventValidator);
        faultEventValidator.validate(event, bindingResult);

        Assert.assertTrue(bindingResult.hasErrors());
        Assert.assertNotNull(bindingResult.getFieldError("gateType"));
    }

    @Test
    void validateTypes_intermediate_unusedGate_shouldReturnError() {
        FaultEvent event = new FaultEvent();
        event.setUri(Generator.generateUri());
        event.setEventType(EventType.INTERMEDIATE);
        event.setGateType(GateType.UNUSED);

        BindingResult bindingResult = ValidationTestUtils.createBinding(event, faultEventValidator);
        faultEventValidator.validate(event, bindingResult);

        Assert.assertTrue(bindingResult.hasErrors());
        Assert.assertNotNull(bindingResult.getFieldError("gateType"));
    }

    @Test
    void validateTypes_nonIntermediate_gateNotUnused_shouldReturnError() {
        FaultEvent event = new FaultEvent();
        event.setUri(Generator.generateUri());
        event.setEventType(EventType.BASIC);
        event.setGateType(GateType.AND);

        BindingResult bindingResult = ValidationTestUtils.createBinding(event, faultEventValidator);
        faultEventValidator.validate(event, bindingResult);

        Assert.assertTrue(bindingResult.hasErrors());
        Assert.assertNotNull(bindingResult.getFieldError("gateType"));
    }

}