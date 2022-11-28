package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.environment.Generator;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.util.EventType;
import cz.cvut.kbss.analysis.model.util.GateType;
import cz.cvut.kbss.jopa.model.EntityManager;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {FaultEventDao.class})
class FaultEventDaoTest extends BaseDaoTestRunner{

    @Autowired
    private EntityManager em;

    @Autowired
    private FaultEventDao faultEventDao;

    @Test
    public void isChild_isChild_shouldReturnTrue() {
        FaultEvent parent = new FaultEvent();
        parent.setName("parentEvent");
        parent.setEventType(EventType.INTERMEDIATE);
        parent.setGateType(GateType.AND);
        parent.setUri(Generator.generateUri());

        FaultEvent child = new FaultEvent();
        child.setName("childEvent");
        child.setEventType(EventType.BASIC);
        child.setGateType(GateType.UNUSED);
        child.setUri(Generator.generateUri());

        parent.addChild(child);


        transactional(() -> em.persist(parent));

        boolean result = faultEventDao.isChild(child.getUri());

       assertTrue(result);
    }

    @Test
    public void isChild_isNot_shouldReturnFalse() {
        FaultEvent notChild = new FaultEvent();
        notChild.setName("event");
        notChild.setEventType(EventType.BASIC);
        notChild.setGateType(GateType.UNUSED);
        notChild.setUri(Generator.generateUri());

        transactional(() -> em.persist(notChild));

        boolean result = faultEventDao.isChild(notChild.getUri());

       assertFalse(result);
    }

}