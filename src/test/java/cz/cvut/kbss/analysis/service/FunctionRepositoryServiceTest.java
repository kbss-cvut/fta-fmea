package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FunctionDao;
import cz.cvut.kbss.analysis.environment.Generator;
import cz.cvut.kbss.analysis.model.Function;
import cz.cvut.kbss.analysis.service.validation.FunctionValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

public class FunctionRepositoryServiceTest {
    @Mock
    FunctionDao functionDao;
    @Mock
    FunctionValidator functionValidator;
    @Captor
    ArgumentCaptor<Function> functionCaptor;

    @InjectMocks
    FunctionRepositoryService functionRepositoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void findWithPropagation_shouldFindTree_shouldUpdateProbabilities() {
        Function function = new Function();
        function.setUri(Generator.generateUri());
        function.setName("Test function");


        Mockito.when(functionValidator.supports(any())).thenReturn(true);

        functionRepositoryService.persist(function);

        Mockito.verify(functionValidator, times(1)).validate(eq(function), any());
    }

    @Test
    void addFunction_shouldAddFunction_shouldCallUpdate() {
        Function function = new Function();
        function.setUri(Generator.generateUri());

        Function dependentFunction = new Function();
        dependentFunction.setUri(Generator.generateUri());

        Mockito.when(functionDao.find(eq(function.getUri()))).thenReturn(Optional.of(function));
        Mockito.when(functionDao.find(eq(dependentFunction.getUri()))).thenReturn(Optional.of(dependentFunction));
        Mockito.when(functionDao.exists(eq(function.getUri()))).thenReturn(true);
        Mockito.when(functionValidator.supports(any())).thenReturn(true);
        Mockito.when(functionDao.update(eq(function))).thenReturn(function);

        functionRepositoryService.addRequiredFunction(function.getUri(), dependentFunction.getUri());
        Mockito.verify(functionDao).update(functionCaptor.capture());

        Function functionToUpdate = functionCaptor.getValue();
        Assertions.assertTrue(functionToUpdate.getRequiredFunctions().contains(dependentFunction));
    }

    @Test
    void addFunctions_shouldAddFunctions_shouldCallUpdate() {
        Function function = new Function();
        function.setUri(Generator.generateUri());

        Function dependentFunction = new Function();
        dependentFunction.setUri(Generator.generateUri());

        Function dependentFunction2 = new Function();
        dependentFunction2.setUri(Generator.generateUri());

        Function dependentFunction3 = new Function();
        dependentFunction3.setUri(Generator.generateUri());

        Mockito.when(functionDao.find(eq(function.getUri()))).thenReturn(Optional.of(function));
        Mockito.when(functionDao.find(eq(dependentFunction.getUri()))).thenReturn(Optional.of(dependentFunction));
        Mockito.when(functionDao.find(eq(dependentFunction2.getUri()))).thenReturn(Optional.of(dependentFunction2));
        Mockito.when(functionDao.find(eq(dependentFunction3.getUri()))).thenReturn(Optional.of(dependentFunction3));
        Mockito.when(functionDao.exists(eq(function.getUri()))).thenReturn(true);
        Mockito.when(functionValidator.supports(any())).thenReturn(true);
        Mockito.when(functionDao.update(eq(function))).thenReturn(function);

        functionRepositoryService.addRequiredFunction(function.getUri(), dependentFunction.getUri());
        functionRepositoryService.addRequiredFunction(function.getUri(), dependentFunction2.getUri());
        functionRepositoryService.addRequiredFunction(function.getUri(), dependentFunction3.getUri());

        Mockito.verify(functionDao,times(3)).update(functionCaptor.capture());

        Function functionToUpdate = functionCaptor.getValue();
        Assertions.assertTrue(functionToUpdate.getRequiredFunctions().contains(dependentFunction));
        Assertions.assertTrue(functionToUpdate.getRequiredFunctions().contains(dependentFunction2));
        Assertions.assertTrue(functionToUpdate.getRequiredFunctions().contains(dependentFunction3));
        Assertions.assertEquals(3,functionToUpdate.getRequiredFunctions().size());
    }

    @Test
    void deleteFunction_shouldRemoveFromFunctions_shouldCallUpdate() {
        Function function = new Function();
        function.setUri(Generator.generateUri());

        Function dependentFunction = new Function();
        dependentFunction.setUri(Generator.generateUri());

        function.addFunction(dependentFunction);

        Mockito.when(functionDao.find(eq(function.getUri()))).thenReturn(Optional.of(function));
        Mockito.when(functionDao.find(eq(dependentFunction.getUri()))).thenReturn(Optional.of(dependentFunction));
        Mockito.when(functionDao.exists(eq(function.getUri()))).thenReturn(true);
        Mockito.when(functionValidator.supports(any())).thenReturn(true);
        Mockito.when(functionDao.update(eq(function))).thenReturn(function);

        functionRepositoryService.deleteFunction(function.getUri(), dependentFunction.getUri());

        Mockito.verify(functionDao).update(function);
        Assertions.assertFalse(function.getRequiredFunctions().contains(function));
    }

    @Test
    void deleteFunctions_shouldRemoveFromFunctions_shouldCallUpdate() {
        Function function = new Function();
        function.setUri(Generator.generateUri());

        Function dependentFunction = new Function();
        dependentFunction.setUri(Generator.generateUri());

        Function dependentFunction2 = new Function();
        dependentFunction2.setUri(Generator.generateUri());

        Function dependentFunction3 = new Function();
        dependentFunction3.setUri(Generator.generateUri());

        Mockito.when(functionDao.find(eq(function.getUri()))).thenReturn(Optional.of(function));
        Mockito.when(functionDao.find(eq(dependentFunction.getUri()))).thenReturn(Optional.of(dependentFunction));
        Mockito.when(functionDao.find(eq(dependentFunction2.getUri()))).thenReturn(Optional.of(dependentFunction2));
        Mockito.when(functionDao.find(eq(dependentFunction3.getUri()))).thenReturn(Optional.of(dependentFunction3));
        Mockito.when(functionDao.exists(eq(function.getUri()))).thenReturn(true);
        Mockito.when(functionValidator.supports(any())).thenReturn(true);
        Mockito.when(functionDao.update(eq(function))).thenReturn(function);

        functionRepositoryService.addRequiredFunction(function.getUri(), dependentFunction.getUri());
        functionRepositoryService.addRequiredFunction(function.getUri(), dependentFunction2.getUri());
        functionRepositoryService.addRequiredFunction(function.getUri(), dependentFunction3.getUri());

        functionRepositoryService.deleteFunction(function.getUri(), dependentFunction.getUri());
        functionRepositoryService.deleteFunction(function.getUri(), dependentFunction2.getUri());

        Mockito.verify(functionDao,times(5)).update(function);
        Assertions.assertFalse(function.getRequiredFunctions().contains(dependentFunction));
        Assertions.assertFalse(function.getRequiredFunctions().contains(dependentFunction2));
        Assertions.assertTrue(function.getRequiredFunctions().contains(dependentFunction3));
        Assertions.assertEquals(1,function.getRequiredFunctions().size());
    }


}
