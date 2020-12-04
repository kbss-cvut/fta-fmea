package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FaultTreeDao;
import cz.cvut.kbss.analysis.environment.Generator;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.FaultTree;
import cz.cvut.kbss.analysis.service.validation.FaultEventValidator;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class FaultTreeRepositoryServiceTest {

    @Mock
    FaultTreeDao faultTreeDao;
    @Mock
    FaultEventValidator faultEventValidator;
    @Mock
    FaultEventRepositoryService faultEventRepositoryService;

    @InjectMocks
    FaultTreeRepositoryService repositoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void findWithPropagation_shouldFindTree_shouldUpdateProbabilities() {
        FaultTree tree = new FaultTree();
        tree.setUri(Generator.generateUri());
        tree.setManifestingEvent(new FaultEvent());

        Mockito.when(faultTreeDao.find(tree.getUri())).thenReturn(Optional.of(tree));
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

        repositoryService.persist(tree);

        Mockito.verify(faultEventValidator).validateTypes(tree.getManifestingEvent());
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

        repositoryService.persist(tree);

        Mockito.verify(faultEventValidator).validateTypes(tree.getManifestingEvent());
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
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.contains(nonTreeEvent));
    }

}