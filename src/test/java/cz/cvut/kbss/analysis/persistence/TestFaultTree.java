package cz.cvut.kbss.analysis.persistence;

import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.model.util.EventType;
import cz.cvut.kbss.analysis.model.util.GateType;
import cz.cvut.kbss.jopa.Persistence;
import cz.cvut.kbss.jopa.model.*;
import cz.cvut.kbss.ontodriver.sesame.config.SesameOntoDriverProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.lang.System;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestFaultTree {


    protected static EntityManagerFactory emf;
    protected EntityManager em;

    @BeforeAll
    public static void initForAll(){

        Map<String, String> conf = new HashMap<>();
        conf.put(JOPAPersistenceProperties.SCAN_PACKAGE, "cz.cvut.kbss.analysis.model");
        conf.put(JOPAPersistenceProperties.ONTOLOGY_PHYSICAL_URI_KEY, "mem:testRepo");
        conf.put(JOPAPersistenceProperties.LANG, "en");
        conf.put(JOPAPersistenceProperties.DATA_SOURCE_CLASS, "cz.cvut.kbss.ontodriver.sesame.SesameDataSource");
        conf.put(PersistenceProperties.JPA_PERSISTENCE_PROVIDER, JOPAPersistenceProvider.class.getName());
        conf.put(SesameOntoDriverProperties.SESAME_USE_VOLATILE_STORAGE, "true");
        emf = Persistence.createEntityManagerFactory("testPU", conf);

        final User u = new User();
        u.setUsername("name");
        u.setPassword("pass");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(u);
        em.getTransaction().commit();

        Authentication a = new AbstractAuthenticationToken(Collections.EMPTY_LIST) {
            @Override
            public Object getCredentials() {
                return u.getPassword();
            }

            @Override
            public Object getPrincipal() {
                return u;
            }
        };
        SecurityContext context = new SecurityContextImpl(a);
        SecurityContextHolder.setContext(context);
    }

    @BeforeEach
    public void init(){
        if(em != null && em.isOpen()) {
            em.close();
        }
        em = emf.createEntityManager();
    }

    @Test
    public void testTest(){
        Function f = new Function();
        f.setName("haha");
        em.getTransaction().begin();
        em.persist(f);
        em.getTransaction().commit();
        Function f2 = em.find(Function.class, f.getUri());
        assertEquals(f.getUri(), f2.getUri());
        assertEquals(f.getName(), f2.getName());
        System.out.println(String.format("<%s> . name = '%s'",f2.getUri().toASCIIString(), f2.getName()));
    }



    @Test
    public void persistTree(){
        em.getTransaction().begin();
        FaultTree ft = new FaultTree();
        ft.setName("ft1");
        ft.setManifestingEvent(node("n1", EventType.BASIC));
        FaultEvent r = ft.getManifestingEvent();
        r.addChild(node("n2", EventType.BASIC));
        r.addChild(node("n3", EventType.BASIC));
        em.persist(ft);
        em.getTransaction().commit();;
        readTree(ft.getUri().toASCIIString());
    }

//    @Test
    public void readTree(String uri){
        FaultTree ft = em.find(FaultTree.class, uri);
        System.out.println(ft.getManifestingEvent().getName());
        ft.getManifestingEvent().getChildren().forEach(n -> System.out.println("\t" + n.getName()));
    }




    protected FaultEvent node(String name, EventType et){
        RiskPriorityNumber rpn = new RiskPriorityNumber();
        FaultEvent fe = new FaultEvent();
        fe.setEventType(et);
        fe.setName(name);
//        fe.setRiskPriorityNumber(rpn);
        fe.setGateType(GateType.AND);
        em.persist(fe);
        return fe;
    }
}
