package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.environment.Generator;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.FaultTree;
import cz.cvut.kbss.analysis.model.util.EventType;
import cz.cvut.kbss.analysis.model.util.GateType;
import cz.cvut.kbss.jopa.model.EntityManager;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ContextConfiguration(classes = {FaultTreeDao.class, FaultEventDao.class})
class FaultTreeDaoTest extends BaseDaoTestRunner {

    @Autowired
    private EntityManager em;

    @Autowired
    private FaultTreeDao faultTreeDao;

    @Autowired
    private FaultEventDao faultEventDao;

    @Test
    public void isRootEvent_isRoot_shouldReturnTrue() {
        FaultTree tree = new FaultTree();
        tree.setUri(Generator.generateUri());
        tree.setName("aTree");

        FaultEvent rootEvent = new FaultEvent();
        rootEvent.setName("rootEvent");
        rootEvent.setEventType(EventType.INTERMEDIATE);
        rootEvent.setGateType(GateType.AND);
        rootEvent.setUri(Generator.generateUri());

        tree.setManifestingEvent(rootEvent);

        transactional(() -> em.persist(tree));

        boolean result = faultTreeDao.isRootEvent(rootEvent.getUri());

       assertTrue(result);
    }

    @Test
    public void isRootEvent_isNot_shouldReturnFalse() {
        FaultTree tree = new FaultTree();
        tree.setUri(Generator.generateUri());
        tree.setName("aTree");

        FaultEvent rootEvent = new FaultEvent();
        rootEvent.setName("rootEvent");
        rootEvent.setEventType(EventType.INTERMEDIATE);
        rootEvent.setGateType(GateType.AND);
        rootEvent.setUri(Generator.generateUri());

        FaultEvent child = new FaultEvent();
        child.setName("child");
        child.setEventType(EventType.BASIC);
        child.setGateType(GateType.UNUSED);
        child.setUri(Generator.generateUri());

        rootEvent.addChild(child);

        tree.setManifestingEvent(rootEvent);

        transactional(() -> em.persist(tree));

        boolean result = faultTreeDao.isRootEvent(child.getUri());

       assertFalse(result);
    }

    @Test // simulation of symmetric branches not queried in the application
    public void symmetricBranches_shouldQueryAll() {
        // create tree with root in A
        FaultTree tree = new FaultTree();
        tree.setUri(Generator.generateUri());
        tree.setName("aTree");

        FaultEvent A = createEvent("A");
        FaultEvent B = createEvent("B");
        FaultEvent C = createEvent("C");

        tree.setManifestingEvent(A);
        transactional(() -> faultTreeDao.persist(tree));

        A.addChild(B);
        A.addChild(C);
        transactional(() -> faultEventDao.update(A));
        transactional(() -> faultTreeDao.update(tree)); // probabilities propagation update (simulation)

        // create subtree
        FaultEvent X = createEvent("X");
        FaultEvent Y = createEvent("Y");
        FaultEvent Z = createEvent("Z");
        X.addChild(Y);
        X.addChild(Z);
        transactional(() -> faultEventDao.persist(X));
        transactional(() -> faultTreeDao.update(tree)); // probabilities propagation update (simulation)

        // add subtree below B
        B.addChild(X);
        transactional(() -> faultEventDao.update(B));
        transactional(() -> faultTreeDao.update(tree)); // probabilities propagation update (simulation)

        // add subtree below C
        C.addChild(X);
        transactional(() -> faultEventDao.update(C));
        transactional(() -> faultTreeDao.update(tree)); // probabilities propagation update (simulation)

        Optional<FaultTree> optionalTree = faultTreeDao.find(tree.getUri());
        if(optionalTree.isEmpty())fail("Tree was not queried");

        FaultTree queriedTree = optionalTree.get();
        FaultEvent manifestingEvent = queriedTree.getManifestingEvent();
       assertNotNull(manifestingEvent);

        Set<FaultEvent> rootChildren = manifestingEvent.getChildren();
       assertFalse(rootChildren.isEmpty());

        rootChildren.forEach(child -> {
           assertEquals(1, child.getChildren().size());
            FaultEvent childX = child.getChildren().iterator().next();
           assertEquals("X", childX.getName());

            Set<FaultEvent> xChildren = childX.getChildren();
           assertFalse(xChildren.isEmpty());

            Set<String> xChildrenNames = xChildren.stream().map(FaultEvent::getName).collect(Collectors.toSet());
           assertTrue(xChildrenNames.contains("Y"));
           assertTrue(xChildrenNames.contains("Z"));
        });
    }

    private FaultEvent createEvent(String name) {
        FaultEvent event = new FaultEvent();
        event.setName(name);
        event.setEventType(EventType.INTERMEDIATE);
        event.setGateType(GateType.AND);
        event.setUri(Generator.generateUri());
        return event;
    }

}