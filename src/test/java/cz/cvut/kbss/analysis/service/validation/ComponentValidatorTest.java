package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.dao.ComponentDao;
import cz.cvut.kbss.analysis.environment.Generator;
import cz.cvut.kbss.analysis.exception.ValidationException;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.util.Vocabulary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ComponentValidatorTest {

    @Mock
    ComponentDao componentDao;

    @InjectMocks
    ComponentValidator componentValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void validateDuplicates_duplicateExists_shouldThrowException() {
        Component component = new Component();
        component.setName("Duplicate Name");
        component.setUri(Generator.generateUri());

        Mockito.when(componentDao.existsWithPredicate(Vocabulary.s_p_hasName, component.getName())).thenReturn(true);

        assertThrows(ValidationException.class, () -> componentValidator.validateDuplicates(component));
    }

    @Test
    void validateDuplicates_noDuplicate_shouldJustRun() {
        Component component = new Component();
        component.setName("Valid Name");
        component.setUri(Generator.generateUri());

        Mockito.when(componentDao.existsWithPredicate(Vocabulary.s_p_hasName, component.getName())).thenReturn(false);

        componentValidator.validateDuplicates(component);
    }

}