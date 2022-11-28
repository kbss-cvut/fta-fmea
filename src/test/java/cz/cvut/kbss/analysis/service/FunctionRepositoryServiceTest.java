package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FunctionDao;
import cz.cvut.kbss.analysis.environment.Generator;
import cz.cvut.kbss.analysis.model.Function;
import cz.cvut.kbss.analysis.service.validation.FunctionValidator;
import static org.junit.jupiter.api.Assertions.*;
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

        functionRepositoryService.addRequiredBehavior(function.getUri(), dependentFunction.getUri());
        Mockito.verify(functionDao).update(functionCaptor.capture());

        Function functionToUpdate = functionCaptor.getValue();
       assertTrue(functionToUpdate.getRequiredBehaviors().contains(dependentFunction));
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

        functionRepositoryService.addRequiredBehavior(function.getUri(), dependentFunction.getUri());
        functionRepositoryService.addRequiredBehavior(function.getUri(), dependentFunction2.getUri());
        functionRepositoryService.addRequiredBehavior(function.getUri(), dependentFunction3.getUri());

        Mockito.verify(functionDao,times(3)).update(functionCaptor.capture());

        Function functionToUpdate = functionCaptor.getValue();
       assertTrue(functionToUpdate.getRequiredBehaviors().contains(dependentFunction));
       assertTrue(functionToUpdate.getRequiredBehaviors().contains(dependentFunction2));
       assertTrue(functionToUpdate.getRequiredBehaviors().contains(dependentFunction3));
       assertEquals(3,functionToUpdate.getRequiredBehaviors().size());
    }

    @Test
    void deleteFunction_shouldRemoveFromFunctions_shouldCallUpdate() {
        Function function = new Function();
        function.setUri(Generator.generateUri());

        Function dependentFunction = new Function();
        dependentFunction.setUri(Generator.generateUri());

        function.addRequiredBehavior(dependentFunction);

        Mockito.when(functionDao.find(eq(function.getUri()))).thenReturn(Optional.of(function));
        Mockito.when(functionDao.find(eq(dependentFunction.getUri()))).thenReturn(Optional.of(dependentFunction));
        Mockito.when(functionDao.exists(eq(function.getUri()))).thenReturn(true);
        Mockito.when(functionValidator.supports(any())).thenReturn(true);
        Mockito.when(functionDao.update(eq(function))).thenReturn(function);

        functionRepositoryService.deleteRequiredBehavior(function.getUri(), dependentFunction.getUri());

        Mockito.verify(functionDao).update(function);
       assertFalse(function.getRequiredBehaviors().contains(function));
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

        functionRepositoryService.addRequiredBehavior(function.getUri(), dependentFunction.getUri());
        functionRepositoryService.addRequiredBehavior(function.getUri(), dependentFunction2.getUri());
        functionRepositoryService.addRequiredBehavior(function.getUri(), dependentFunction3.getUri());

        functionRepositoryService.deleteRequiredBehavior(function.getUri(), dependentFunction.getUri());
        functionRepositoryService.deleteRequiredBehavior(function.getUri(), dependentFunction2.getUri());

        Mockito.verify(functionDao,times(5)).update(function);
       assertFalse(function.getRequiredBehaviors().contains(dependentFunction));
       assertFalse(function.getRequiredBehaviors().contains(dependentFunction2));
       assertTrue(function.getRequiredBehaviors().contains(dependentFunction3));
       assertEquals(1,function.getRequiredBehaviors().size());
    }


}
