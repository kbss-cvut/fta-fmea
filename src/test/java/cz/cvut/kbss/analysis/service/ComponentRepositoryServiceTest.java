package cz.cvut.kbss.analysis.service;


import cz.cvut.kbss.analysis.dao.ComponentDao;
import cz.cvut.kbss.analysis.dto.update.ComponentUpdateDTO;
import cz.cvut.kbss.analysis.environment.Generator;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.Function;
import cz.cvut.kbss.analysis.service.validation.NamedEntityValidator;
import cz.cvut.kbss.analysis.service.validation.groups.ValidationScopes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;

class ComponentRepositoryServiceTest {

    @Mock
    ComponentDao componentDao;
    @Mock
    NamedEntityValidator<Component> componentValidator;
    @Captor
    ArgumentCaptor<Component> componentCaptor;

    @InjectMocks
    ComponentRepositoryService repositoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void persist_shouldCallPrePersist() {
        Component component = new Component();
        component.setUri(Generator.generateUri());

        Mockito.when(componentValidator.supports(any())).thenReturn(true);

        repositoryService.persist(component);

        Mockito.verify(componentValidator, Mockito.times(1)).validate(eq(component), any(), eq(ValidationScopes.Create.class));
    }

    @Test
    void updateByDTO_shouldFind_shouldCopyData_shouldCallUpdate() {
        ComponentUpdateDTO updateDTO = new ComponentUpdateDTO();
        updateDTO.setUri(Generator.generateUri());
        updateDTO.setName("UpdatedName");

        Component component = new Component();
        component.setUri(updateDTO.getUri());
        component.setName("Old name");

        Mockito.when(componentDao.find(eq(updateDTO.getUri()))).thenReturn(Optional.of(component));
        Mockito.when(componentDao.exists(eq(updateDTO.getUri()))).thenReturn(true);
        Mockito.when(componentValidator.supports(any())).thenReturn(true);
        Mockito.when(componentDao.update(eq(component))).thenReturn(component);

        Component result = repositoryService.updateByDTO(updateDTO);

        Mockito.verify(componentDao).update(component);
        Assertions.assertEquals(component.getUri(), result.getUri());
        Assertions.assertEquals(updateDTO.getName(), result.getName());
    }

    @Test
    void addFunction_shouldAddFunction_shouldCallUpdate() {
        Component component = new Component();
        component.setUri(Generator.generateUri());

        Function function = new Function();
        function.setUri(Generator.generateUri());

        Mockito.when(componentDao.find(eq(component.getUri()))).thenReturn(Optional.of(component));
        Mockito.when(componentDao.exists(eq(component.getUri()))).thenReturn(true);
        Mockito.when(componentValidator.supports(any())).thenReturn(true);
        Mockito.when(componentDao.update(eq(component))).thenReturn(component);

        repositoryService.addFunction(component.getUri(), function);

        Mockito.verify(componentDao).update(componentCaptor.capture());

        Component componentToUpdate = componentCaptor.getValue();
        Assertions.assertTrue(componentToUpdate.getFunctions().contains(function));
    }

    @Test
    void deleteFunction_shouldRemoveFromFunctions_shouldCallUpdate() {
        Component component = new Component();
        component.setUri(Generator.generateUri());

        Function function = new Function();
        function.setUri(Generator.generateUri());
        component.addFunction(function);

        Mockito.when(componentDao.find(eq(component.getUri()))).thenReturn(Optional.of(component));
        Mockito.when(componentDao.exists(eq(component.getUri()))).thenReturn(true);
        Mockito.when(componentValidator.supports(any())).thenReturn(true);
        Mockito.when(componentDao.update(eq(component))).thenReturn(component);

        repositoryService.deleteFunction(component.getUri(), function.getUri());

        Mockito.verify(componentDao).update(component);
        Assertions.assertFalse(component.getFunctions().contains(function));
    }

    @Test
    void linkComponents_shouldFindComponents_shouldSetParentComponent_shouldCallUpdate() {
        Component component = new Component();
        component.setUri(Generator.generateUri());

        Component linkComponent = new Component();
        linkComponent.setUri(Generator.generateUri());

        Mockito.when(componentDao.find(eq(component.getUri()))).thenReturn(Optional.of(component));
        Mockito.when(componentDao.find(eq(linkComponent.getUri()))).thenReturn(Optional.of(linkComponent));

        Mockito.when(componentDao.exists(eq(component.getUri()))).thenReturn(true);
        Mockito.when(componentDao.exists(eq(linkComponent.getUri()))).thenReturn(true);
        Mockito.when(componentValidator.supports(any())).thenReturn(true);
        Mockito.when(componentDao.update(eq(component))).thenReturn(component);
        Mockito.when(componentDao.update(eq(linkComponent))).thenReturn(linkComponent);

        repositoryService.linkComponents(component.getUri(), linkComponent.getUri());

        Assertions.assertEquals(linkComponent, component.getParentComponent());
        Assertions.assertTrue(linkComponent.getComponents() != null );
        Assertions.assertTrue(linkComponent.getComponents().size() == 1);
        Assertions.assertTrue(linkComponent.getComponents().contains( component));
    }

    @Test
    void linkComponents_shouldFindComponent_shouldSetParentNull_shouldCallUpdate() {
        Component component = new Component();
        component.setUri(Generator.generateUri());
        Component parent = new Component();
        parent.setUri(Generator.generateUri());
        component.setParentComponent(parent);

        Mockito.when(componentDao.find(eq(component.getUri()))).thenReturn(Optional.of(component));
        Mockito.when(componentDao.exists(eq(component.getUri()))).thenReturn(true);
        Mockito.when(componentValidator.supports(any())).thenReturn(true);

        Mockito.when(componentDao.update(eq(component))).thenReturn(component);

        repositoryService.unlinkComponents(component.getUri());

        Mockito.verify(componentDao).update(component);
        Assertions.assertNull(component.getParentComponent());
    }

}