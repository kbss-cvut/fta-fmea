package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.SecurityConf;
import cz.cvut.kbss.analysis.environment.Generator;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.diagram.Rectangle;
import cz.cvut.kbss.analysis.model.fta.FtaEventType;
import cz.cvut.kbss.analysis.model.fta.GateType;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {FaultEventDao.class, UserDao.class, SecurityConf.class})
class FaultEventDaoTest extends BaseDaoTestRunner{

    @Autowired
    private EntityManager em;

    @Autowired
    private FaultEventDao faultEventDao;

    @Test
    public void isChild_isChild_shouldReturnTrue() {
        FaultEvent parent = new FaultEvent();
        parent.setName("parentEvent");
        parent.setEventType(FtaEventType.INTERMEDIATE);
        parent.setGateType(GateType.AND);
        parent.setUri(Generator.generateUri());

        FaultEvent child = new FaultEvent();
        child.setName("childEvent");
        child.setEventType(FtaEventType.BASIC);
        child.setUri(Generator.generateUri());

        parent.addChild(child);


        transactional(() -> em.persist(parent));

        boolean result = faultEventDao.isChild(child.getUri());

        Assertions.assertTrue(result);
    }

    @Test
    public void isChild_isNot_shouldReturnFalse() {
        FaultEvent notChild = new FaultEvent();
        notChild.setName("event");
        notChild.setEventType(FtaEventType.BASIC);
        notChild.setUri(Generator.generateUri());

        transactional(() -> em.persist(notChild));

        boolean result = faultEventDao.isChild(notChild.getUri());

        Assertions.assertFalse(result);
    }

    @Test
    void testUpdateRectangle(){
        Rectangle r = new Rectangle(1.,1.,2.,2.);

        transactional(() -> em.persist(r));

        double newVal = 10.;

        transactional(() -> {
            Rectangle r1 = new Rectangle();
            r1.setUri(r.getUri());
            r1.setX(newVal);
            r1.setY(r.getY());
            r1.setWidth(r.getWidth());
            r1.setHeight(r.getHeight());
            faultEventDao.update(r1);
        });

        Rectangle r1 = em.find(Rectangle.class, r.getUri());
        Assertions.assertEquals(r1.getX(), newVal);
    }

}