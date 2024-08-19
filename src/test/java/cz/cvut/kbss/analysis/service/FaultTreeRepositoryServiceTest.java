package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FaultTreeDao;
import cz.cvut.kbss.analysis.environment.Generator;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.FaultEventType;
import cz.cvut.kbss.analysis.model.FaultTree;
import cz.cvut.kbss.analysis.service.security.SecurityUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class FaultTreeRepositoryServiceTest {

    @Mock
    FaultTreeDao faultTreeDao;
    @Mock
    Validator validator;
    @Mock
    FaultEventRepositoryService faultEventRepositoryService;

    @InjectMocks
    FaultTreeRepositoryService repositoryService;

    @Mock
    SecurityUtils securityUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void findWithPropagation_shouldFindTree_shouldUpdateProbabilities() {
        FaultTree tree = new FaultTree();
        tree.setUri(Generator.generateUri());
        tree.setManifestingEvent(new FaultEvent());
        FaultEventType fet = new FaultEventType();
        fet.setAuxiliary(true);
        tree.getManifestingEvent().setSupertypes(new HashSet<>());
        tree.getManifestingEvent().getSupertypes().add(fet);

        Mockito.when(faultTreeDao.find(tree.getUri())).thenReturn(Optional.of(tree));
        Mockito.when(faultTreeDao.exists(tree.getUri())).thenReturn(true);
        Mockito.when(validator.supports(any())).thenReturn(true);
        Mockito.when(faultTreeDao.update(tree)).thenReturn(tree);

        repositoryService.findWithPropagation(tree.getUri());

        Mockito.verify(faultEventRepositoryService, Mockito.atLeastOnce()).propagateProbability(tree.getManifestingEvent());
    }

    @Test
    void persist_shouldCallPrePersist_shouldValidateTypes_shouldPersist() {
        FaultTree tree = new FaultTree();
        tree.setUri(Generator.generateUri());
        tree.setManifestingEvent(new FaultEvent());

        Mockito.when(faultTreeDao.find(tree.getUri())).thenReturn(Optional.of(tree));
        Mockito.when(validator.supports(any())).thenReturn(true);

        repositoryService.persist(tree);

        Mockito.verify(validator).validate(eq(tree), any());
        Mockito.verify(faultTreeDao).persist(tree);
    }

    @Test
    void persist_shouldCallPrePersist_shouldValidateTypes_shouldReuseEvent() {
        FaultTree tree = new FaultTree();
        tree.setUri(Generator.generateUri());
        FaultEvent manifestingEvent = new FaultEvent();
        manifestingEvent.setUri(Generator.generateUri());
        tree.setManifestingEvent(manifestingEvent);

        Mockito.when(faultTreeDao.find(tree.getUri())).thenReturn(Optional.of(tree));
        Mockito.when(faultEventRepositoryService.findRequired(manifestingEvent.getUri())).thenReturn(manifestingEvent);
        Mockito.when(validator.supports(any())).thenReturn(true);

        repositoryService.persist(tree);

        Mockito.verify(validator).validate(eq(tree), any());
        Mockito.verify(faultEventRepositoryService).findRequired(manifestingEvent.getUri());
        Mockito.verify(faultTreeDao).persist(tree);
    }

    @Test
    void getReusableEvents_shouldOmitTreeEvents() {
        FaultTree tree = new FaultTree();
        tree.setUri(Generator.generateUri());
        FaultEvent manifestingEvent = new FaultEvent();

        manifestingEvent.addChild(new FaultEvent());
        tree.setManifestingEvent(manifestingEvent);

        List<FaultEvent> events = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            FaultEvent child = new FaultEvent();
            child.setUri(Generator.generateUri());
            events.add(child);
            manifestingEvent.addChild(child);
        }

        FaultEvent nonTreeEvent = new FaultEvent();
        nonTreeEvent.setUri(Generator.generateUri());
        events.add(nonTreeEvent);

        Mockito.when(faultTreeDao.find(tree.getUri())).thenReturn(Optional.of(tree));
        Mockito.when(faultEventRepositoryService.findAll()).thenReturn(events);

        List<FaultEvent> result = repositoryService.getReusableEvents(tree.getUri());

        Mockito.verify(faultEventRepositoryService).findAll();
        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(result.contains(nonTreeEvent));
    }

}