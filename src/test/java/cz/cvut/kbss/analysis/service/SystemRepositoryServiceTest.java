package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.SystemDao;
import cz.cvut.kbss.analysis.environment.Generator;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.System;
import cz.cvut.kbss.analysis.service.security.SecurityUtils;
import cz.cvut.kbss.analysis.service.validation.EntityValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class SystemRepositoryServiceTest {

    @Mock
    SystemDao systemDao;
    @Mock
    ComponentRepositoryService componentRepositoryService;
    @Mock
    SecurityUtils securityUtils;
    @Mock
    EntityValidator validator;
    @InjectMocks
    SystemRepositoryService repositoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void rename_shouldFind_shouldCopyData_shouldCallUpdate() {
        System systemRename = new System();
        systemRename.setUri(Generator.generateUri());
        systemRename.setName("UpdatedName");

        System system = new System();
        systemRename.setUri(systemRename.getUri());
        systemRename.setName("oldName");

        Mockito.when(systemDao.find(eq(systemRename.getUri()))).thenReturn(Optional.of(system));
        Mockito.when(systemDao.exists(eq(system.getUri()))).thenReturn(true);
        Mockito.when(validator.supports(any())).thenReturn(true);
        Mockito.when(systemDao.update(eq(system))).thenReturn(system);

        repositoryService.rename(systemRename);

        Mockito.verify(systemDao).update(system);
        Assertions.assertEquals(systemRename.getName(), system.getName());
    }

    @Test
    void addComponent_shouldFindSystem_shouldFindComponent_shouldCallUpdate() {
        System system = new System();
        system.setUri(Generator.generateUri());

        Component component = new Component();
        component.setUri(Generator.generateUri());

        Mockito.when(systemDao.find(eq(system.getUri()))).thenReturn(Optional.of(system));
        Mockito.when(componentRepositoryService.findRequired(eq(component.getUri()))).thenReturn(component);
        Mockito.when(systemDao.exists(eq(system.getUri()))).thenReturn(true);
        Mockito.when(validator.supports(any())).thenReturn(true);
        Mockito.when(systemDao.update(eq(system))).thenReturn(system);

        repositoryService.addComponent(system.getUri(), component.getUri());

        Mockito.verify(systemDao).update(system);
        Assertions.assertTrue(system.getComponents().contains(component));
    }

    @Test
    void removeComponent_shouldFindSystem_shouldFindComponent_shouldCallUpdate() {
        System system = new System();
        system.setUri(Generator.generateUri());

        Component component = new Component();
        component.setUri(Generator.generateUri());
        system.addComponent(component);

        Mockito.when(systemDao.find(eq(system.getUri()))).thenReturn(Optional.of(system));
        Mockito.when(componentRepositoryService.findRequired(eq(component.getUri()))).thenReturn(component);
        Mockito.when(systemDao.exists(eq(system.getUri()))).thenReturn(true);
        Mockito.when(validator.supports(any())).thenReturn(true);
        Mockito.when(systemDao.update(eq(system))).thenReturn(system);

        repositoryService.removeComponent(system.getUri(), component.getUri());

        Mockito.verify(systemDao).update(system);
        Assertions.assertFalse(system.getComponents().contains(component));
    }

}