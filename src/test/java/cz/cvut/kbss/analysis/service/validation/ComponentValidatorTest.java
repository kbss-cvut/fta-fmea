package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.dao.ComponentDao;
import cz.cvut.kbss.analysis.model.Component;
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

class ComponentValidatorTest {

    @Mock
    ComponentDao componentDao;
    @Mock
    SpringValidatorAdapter validatorAdapter;

    EntityValidator componentValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(componentDao.getType()).thenReturn(Component.class);
        componentValidator = ValidatorsConfiguration.namedEntityValidator(componentDao, validatorAdapter);
    }

    @Test
    void validateDuplicates_duplicateExists_shouldThrowException() {
        Component component = new Component();
        component.setName("Duplicate Name");

        Mockito.when(componentDao.existsWithPredicate(Vocabulary.s_p_name, component.getName())).thenReturn(true);


        BindingResult bindingResult = ValidationTestUtils.createBinding(component, componentValidator);
        componentValidator.validate(component, bindingResult);

        Assertions.assertTrue(bindingResult.hasErrors());
        Assertions.assertNotNull(bindingResult.getFieldError("name"));
    }

    @Test
    void validateDuplicates_noDuplicate_shouldJustRun() {
        Component component = new Component();
        component.setName("Valid Name");

        Mockito.when(componentDao.existsWithPredicate(Vocabulary.s_p_name, component.getName())).thenReturn(false);

        BindingResult bindingResult = ValidationTestUtils.createBinding(component, componentValidator);
        componentValidator.validate(component, bindingResult);

        Assertions.assertFalse(bindingResult.hasErrors());
    }

}