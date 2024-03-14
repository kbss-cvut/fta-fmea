package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FaultEventDao;
import cz.cvut.kbss.analysis.dao.FaultTreeDao;
import cz.cvut.kbss.analysis.environment.Generator;
import cz.cvut.kbss.analysis.exception.LogicViolationException;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.service.validation.FaultEventValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

public class FaultEventRepositoryServiceTest {

    @Mock
    FaultEventDao faultEventDao;
    @Mock
    FaultEventValidator faultEventValidator;
    @Mock
    ComponentRepositoryService componentRepositoryService;
    @Mock
    FaultTreeDao faultTreeDao;

    @InjectMocks
    FaultEventRepositoryService repositoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void update_shouldCallPreUpdate() {
        FaultEvent event = new FaultEvent();
        event.setUri(Generator.generateUri());

        Mockito.when(faultEventDao.exists(event.getUri())).thenReturn(true);
        Mockito.when(faultEventValidator.supports(any())).thenReturn(true);
        Mockito.when(faultEventDao.update(eq(event))).thenReturn(event);

        repositoryService.update(event);

        Mockito.verify(faultEventValidator).validate(eq(event), any());
    }

    @Test
    void remove_isRootEvent_shouldCallPreRemove_shouldThrowException() {
        FaultEvent event = new FaultEvent();
        event.setUri(Generator.generateUri());

        Mockito.when(faultTreeDao.isRootEvent(eq(event.getUri()))).thenReturn(true);

        assertThrows(LogicViolationException.class, () -> repositoryService.remove(event));
    }

    @Test
    void remove_isNotRootEvent_shouldJustRun() {
        FaultEvent event = new FaultEvent();
        event.setUri(Generator.generateUri());

        Mockito.when(faultTreeDao.isRootEvent(eq(event.getUri()))).thenReturn(false);

        repositoryService.remove(event);

        Mockito.verify(faultEventDao).remove(event);
    }

    @Test
    void addInputEvent_shouldDo2PhaseUpdate() {
        FaultEvent inputEvent = new FaultEvent();
        inputEvent.setUri(Generator.generateUri());

        FaultEvent parentEvent = new FaultEvent();
        parentEvent.setUri(Generator.generateUri());


        Mockito.when(faultEventDao.find(eq(parentEvent.getUri()))).thenReturn(Optional.of(parentEvent));
        Mockito.when(faultEventDao.exists(parentEvent.getUri())).thenReturn(true);
        Mockito.when(faultEventValidator.supports(any())).thenReturn(true);
        Mockito.when(faultEventDao.update(eq(parentEvent))).thenReturn(parentEvent);

        repositoryService.addInputEvent(parentEvent.getUri(), new FaultEvent());

        Mockito.verify(faultEventDao, times(2)).update(parentEvent);
    }

    @Test
    void propagateProbability_nonIntermediate_justReturnValue() {
        FaultEvent event = new FaultEvent();
        event.setProbability(Generator.randomDouble());

        Double result = repositoryService.propagateProbability(event);

        Assertions.assertEquals(event.getProbability(), result);
    }

    @Test
    void getFailureMode_shouldFindEvent_shouldReturnItsFailureMode() {
        FaultEvent event = new FaultEvent();
        event.setUri(Generator.generateUri());
        FailureMode failureMode = new FailureMode();
        failureMode.setUri(Generator.generateUri());
        event.setFailureMode(failureMode);

        Mockito.when(faultEventDao.find(eq(event.getUri()))).thenReturn(Optional.of(event));

        FailureMode result = repositoryService.getFailureMode(event.getUri());
        Assertions.assertEquals(event.getFailureMode(), result);
    }

    @Test
    void addFailureMode_shouldSetData_shouldUpdateEvent() {
        FaultEvent event = new FaultEvent();
        event.setUri(Generator.generateUri());

        FailureMode failureMode = new FailureMode();
        failureMode.setUri(Generator.generateUri());
        Component component = new Component();
        component.setUri(Generator.generateUri());
        failureMode.setItem(component);

        Mockito.when(faultEventDao.find(eq(event.getUri()))).thenReturn(Optional.of(event));
        Mockito.when(componentRepositoryService.findRequired(eq(failureMode.getItem().getUri()))).thenReturn(component);
        Mockito.when(faultEventDao.exists(event.getUri())).thenReturn(true);
        Mockito.when(faultEventValidator.supports(any())).thenReturn(true);
        Mockito.when(faultEventDao.update(eq(event))).thenReturn(event);

        FailureMode result = repositoryService.addFailureMode(event.getUri(), failureMode);

        Mockito.verify(faultEventDao, times(1)).update(event);

        Assertions.assertEquals(failureMode, result);
        Assertions.assertTrue(component.getFailureModes().contains(failureMode));
        Assertions.assertTrue(failureMode.getManifestations().contains(event));
    }

    @Test
    void deleteFailureMode_shouldRemoveFromEventFromFailureModeEffects() {
        FaultEvent event = new FaultEvent();
        event.setUri(Generator.generateUri());

        FailureMode failureMode = new FailureMode();
        failureMode.setUri(Generator.generateUri());
        event.setBehavior(failureMode);

        Mockito.when(faultEventDao.find(eq(event.getUri()))).thenReturn(Optional.of(event));
        Mockito.when(faultEventDao.exists(event.getUri())).thenReturn(true);
        Mockito.when(faultEventValidator.supports(any())).thenReturn(true);
        Mockito.when(faultEventDao.update(eq(event))).thenReturn(event);

        repositoryService.deleteFailureMode(event.getUri());

        Mockito.verify(faultEventDao, times(1)).update(event);
    }

}
