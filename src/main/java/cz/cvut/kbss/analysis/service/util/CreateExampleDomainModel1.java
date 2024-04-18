package cz.cvut.kbss.analysis.service.util;

import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.model.System;
import cz.cvut.kbss.analysis.model.ava.ATASystem;
import cz.cvut.kbss.analysis.model.ava.FHAEventType;
import cz.cvut.kbss.analysis.model.ava.IndependentSNSItem;
import cz.cvut.kbss.analysis.model.ava.SNSComponent;
import cz.cvut.kbss.analysis.model.method.EstimationMethod;
import cz.cvut.kbss.analysis.model.method.VerificationMethod;
import cz.cvut.kbss.analysis.model.util.TypeCategory;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.Descriptor;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@DependsOn(value = {"txManager", "faultTreeController", "faultTreeRepositoryService", "ftaApplication"})
@Slf4j
public class CreateExampleDomainModel1 {//implements ApplicationListener<ContextRefreshedEvent> {

    public static final URI ATOMIC_TYPE = URI.create(Vocabulary.s_c_atomic_event_type);
    public static final URI COMPLEX_TYPE = URI.create(Vocabulary.s_c_complex_event_type);
    private final EntityManager em;

    private Map<String, IndependentSNSItem> regSys = new HashMap<>();
    private Map<String, System> regAc = new HashMap<>();
    private Map<String, SNSComponent> regSns = new HashMap<>();
    private Map<String, ATASystem> regAta = new HashMap<>();
    private Map<String, Function> regFun = new HashMap<>();
    private Map<String, FailureMode> regFM = new HashMap<>();
    private List<VerificationMethod> verificationMethods = verificationMethods();


    private EstimationMethod predictionEstimationMethod;
    private EstimationMethod estimateEstimationMethod;



    public CreateExampleDomainModel1(EntityManager em) {
        this.em = em;
        predictionEstimationMethod = new EstimationMethod();
        predictionEstimationMethod.setName("prediction");
        estimateEstimationMethod = new EstimationMethod();
        estimateEstimationMethod.setName("estimate");
    }

    public void createModel(){
        log.info("post construct - CreateExampleDomainModel1.createModel, em = {}", em);
        System ac = acPartonomy1();
        persist(ac);
    }

//    @Override
//    public void onApplicationEvent(ContextRefreshedEvent event) {
//
//        log.info("post construct - CreateExampleDomainModel1.createModel, em = {}", em);
//        System ac = acPartonomy1();
//        EntityManager em = event.getApplicationContext().getBean(EntityManager.class);
//        persist(ac, em);
//    }

    @Transactional
    public void persist(Item ac){
        _persist(ac);
    }

    public void _persist(Item item){
        Descriptor d = new EntityDescriptor(URI.create("http://context-with-sns-partonomy"));

        Stream<Item> itemStream = depthFirst(item);
//        itemStream.forEach(i -> java.lang.System.out.println(i.getName()));
        List<Item> items = itemStream.collect(Collectors.toList());

        // persist item types
        for(Item it : items){
            if(it.getSupertypes() != null){
                for (Item type : it.getSupertypes()) {
                    _persist(type);
                }
            }
        }

        // persist behavioral  manifestations
        for(Item it : items){

            List<Behavior> behaviors = Stream.of(it.getFailureModes(), it.getFunctions())
                    .filter(s -> s != null)
                    .flatMap(s -> s.stream()).collect(Collectors.toList());

            for(Behavior b : behaviors){
                if(b.getManifestations() != null)
                    for(Event e : b.getManifestations())
                        em.persist(e, d);
                em.persist(b, d);
            }
        }
//
//
//
//
//        Item i = items.get(0);
//        em.persist(, d);
        for (Item i : items) {
            if(i.getUri() == null)
                em.persist(i, d);
        }
    }

    public void persistWithTransaction(System ac, EntityManager em ){
        Descriptor d = new EntityDescriptor(URI.create("http://context-with-sns-partonomy"));

        Stream<Item> itemStream = depthFirst(ac);
//        itemStream.forEach(i -> java.lang.System.out.println(i.getName()));
        boolean inTransaction = em.getTransaction().isActive();
        if(!inTransaction)
            em.getTransaction().begin();
        try {
            for (Item i : itemStream.collect(Collectors.toList())) {
                em.persist(i, d);
            }
        }finally {
            if(!inTransaction)
                em.getTransaction().commit();
        }

    }

    Stream<Item> depthFirst(Item i){
        if(i.getComponents() == null || i.getComponents().isEmpty())
            Stream.of(i);

        return Stream.concat(((Set<Component>)i.getComponents()).stream().flatMap(c -> depthFirst(c)) , Stream.of(i));
    }

    public System acPartonomy1(){
        String acModel = "acm1";
        Pair<String, SNSComponent> s21 = snsComponent(acModel, "Environmental control", "21-00-00", null, null, null, null, null);
        Pair<String, SNSComponent> s21_10 = snsComponent(s21.getFirst(), s21.getSecond(), "Compression", "21-10-00", null, null, null, null, null);
        Pair<String, SNSComponent> s21_10_01 = snsComponent(s21_10.getFirst(), s21_10.getSecond(), "Expansion joint", "21-10-01", "R92.820", 2, "M4114785", Arrays.asList("Ca2", "Ca20"), null);
        Pair<String, SNSComponent> s21_10_02 = snsComponent(s21_10.getFirst(), s21_10.getSecond(), "Air flow limiter", "21-10-02", "44200.09", 1, "M5155470", Arrays.asList("Ca14"), 1./200000);
        Pair<String, SNSComponent> s21_10_03 = snsComponent(s21_10.getFirst(), s21_10.getSecond(), "Filter", "21-10-03", "AO-2016/AV", 1, "M5102422", Arrays.asList("Ca15"), 1./10000);
        Pair<String, SNSComponent> s21_10_03_c1 = snsComponent(s21_10_03.getFirst(), s21_10_03.getSecond(), "Filter element", null, "K2016-AO/AV", 1, null, Arrays.asList("2a"), null);
        Pair<String, SNSComponent> s21_10_03_c1_c1 = snsComponent(s21_10_03_c1.getFirst(), s21_10_03_c1.getSecond(), "Gasket as part of filter elmnt or seprtly", null, "BVK/AV4 NG", 1, null, Arrays.asList("v TP pozice 2b"), null);
        Pair<String, SNSComponent> s21_10_03_c2 = snsComponent(s21_10_03.getFirst(), s21_10_03.getSecond(), "Gasket is not part of elmnt", null, "DAK/AV1 NG", 1, null, Arrays.asList("v TP pozice 1b"), null);
        Pair<String, SNSComponent> s21_10_03_c3 = snsComponent(s21_10_03.getFirst(), s21_10_03.getSecond(), "V-band clamp", null, "BVK/AV13 NG", 1, null, Arrays.asList("v TP pozice 1e"), null);
        Pair<String, SNSComponent> s21_10_03_c4 = snsComponent(s21_10_03.getFirst(), s21_10_03.getSecond(), "Washer", null, "BVK/AV8", 1, null, Arrays.asList("v TP pozice 1c"), null);
        Pair<String, SNSComponent> s21_10_03_c5 = snsComponent(s21_10_03.getFirst(), s21_10_03.getSecond(), "Nut self-locking", null, "BVK/AV30", 1, null, Arrays.asList("v TP pozice 1d"), null);
        Pair<String, SNSComponent> s21_30 = snsComponent(s21.getFirst(), s21.getSecond(), "Pressurization control", "21-30-00", null, null, null, null, null);
        Pair<String, SNSComponent> s21_30_01 = snsComponent(s21_30.getFirst(), s21_30.getSecond(), "Regulator", "21-30-01", "VP-01-039NG-AV", 1, "M5142230", Arrays.asList("Cd1"), 1./60000);
        Pair<String, SNSComponent> s21_30_02 = snsComponent(s21_30.getFirst(), s21_30.getSecond(), "Control valve", "21-30-02", "VP-02-039NG-AV", 1, "M5142232", Arrays.asList("Cd3"), 1./80000);
        Pair<String, SNSComponent> s21_30_02_c1 = snsComponent(s21_30_02.getFirst(), s21_30_02.getSecond(), "Gasket", null, "pn-13", 1, null, null, null);
        Pair<String, SNSComponent> s21_30_03 = snsComponent(s21_30.getFirst(), s21_30.getSecond(), "Control and safety valve", "21-30-03", "VP-03-039NG-AV", 1, "M5142234", Arrays.asList("Cd5"), 1./60000);
        Pair<String, SNSComponent> s21_30_03_c1 = snsComponent(s21_30_03.getFirst(), s21_30_03.getSecond(), "Gasket", null, "pn-14", 1, null, null, null);

        faultEventType(s21_30.getSecond(),
                "1.1 Regulace tlaku v kabině",
                "FC_1.1. Tlak v kabině mimo regulační meze signalizovaný na MFD (CABIN PRESS/Caution)",
                0.0004, 1);
        faultEventType(s21_30.getSecond(),
                "1.1 Regulace tlaku v kabině",
                "FC_1.2. Tlak v kabině mimo krajní meze signalizovaný na MFD (CABIN PRESS/Warning) na table CANOPY CAB PRESS  - podtlak v kabině / překročena max. kabin. výška",
                0.003, 2);
        faultEventType(s21_30.getSecond(),
                "1.1 Regulace tlaku v kabině",
                "FC_1.3. Tlak v kabině mimo krajní meze signalizovaný pouze na table CANOPY CAB PRESS  - podtlak v kabině / překročena max. kabin. výška",
                0.003,3);
        faultEventType(s21_30.getSecond(),
                "1.1 Regulace tlaku v kabině",
                "FC_1.4. Tlak v kabině mimo krajní meze nesignalizovaný na MFD ani na table - podtlak v kabině / překročena max. kabin. výška",
                0.02,4);
        faultEventType(s21_30.getSecond(),
                "1.1 Regulace tlaku v kabině",
                "FC_1.5. Tlak v kabině mimo krajní meze signalizovaný na MFD (CABIN PRESS/Warning) a table CANOPY CAB a na table CANOPY CAB PRESS  - nebezpečný přetlak",
                0.003,1);
        faultEventType(s21_30.getSecond(),
                "1.1 Regulace tlaku v kabině",
                "FC_1.6. Tlak v kabině mimo krajní nesignalizovaný na  MFD, pouze na table - nebezpečný přetlak",
                0.1,2);

        System aircraft = (System)s21.getSecond().getParentComponent();
        java.lang.System.out.println(aircraft);
        return aircraft;
    }

    protected Pair<String, SNSComponent> snsComponent(String acmodel, String label, String ataCode, String partNumber, Integer quantity, String stock, Collection<String> schematicDescription, Double failureRate){
        System ac = regAc.get(acmodel);
        if(ac == null){
            ac = new System();
            ac.setName(acmodel);
            ac.setComponents(new HashSet<>());
            regAc.put(acmodel, ac);
        }
        String componentKey = componentKey(acmodel, ataCode, partNumber);

        SNSComponent c = regSns.get(componentKey);
        if(c == null){
            c = snsComponent(label, ataCode, partNumber, quantity, stock, schematicDescription, failureRate);
            regSns.put(componentKey, c);
        }
        ac.addComponent(c);

        return Pair.of(componentKey, c);
    }

    protected String componentKey(String prefix, String ataCode, String partNumber){
        return String.format("%s/%s--%s", prefix, norm(ataCode), norm(partNumber));
    }

    protected String norm(String s) {
        return s != null ? s : "";
    }

    protected Pair<String, SNSComponent> snsComponent(String keyPrefix, SNSComponent parent, String label, String ataCode, String partNumber, Integer quantity, String stock, Collection<String> schematicDescription, Double failureRate){
        String componentKey = componentKey(keyPrefix, ataCode, partNumber);
        SNSComponent c = regSns.get(componentKey);
        if(c == null){
            c = snsComponent(label, ataCode, partNumber, quantity, stock, schematicDescription, failureRate);
            regSns.put(componentKey, c);
        }
        parent.addComponent(c);
        c.setParentComponent(parent);

        return Pair.of(componentKey, c);
    }

    protected SNSComponent snsComponent(String label, String ataCode, String partNumber, Integer quantity, String stock, Collection<String> schematicDescription, Double failureRate){
        SNSComponent c = new SNSComponent();
        IndependentSNSItem part = reusableSystem(partNumber, label, stock, failureRate);
        ATASystem ataSystem = ataSystem(label, ataCode);


        c.setSupertypes(new HashSet<>());
        c.getSupertypes().addAll(Stream.of(part, ataSystem).filter(t -> t != null).collect(Collectors.toList()));

        c.setName(label);
        c.setQuantity(quantity);
        if(schematicDescription != null)
            c.setSchematicDescription(new HashSet<>(schematicDescription));
        return c;
    }

    protected ATASystem ataSystem(String label, String ataCode){
        if(ataCode == null)
            return null;

        ATASystem c = regAta.get(ataCode);
        if(c == null){
            c = new ATASystem();
            c.setName(label);
            c.setAtaCode(ataCode);
            c.setTypeCategory(TypeCategory.ROLE);
        }
        return c;
    }

    protected FaultEventType faultEventType(Item i, String function, String failureMode, Double requiredProbability,
                                            Integer criticality){
        FHAEventType faultEventType = new FHAEventType();
        Set<URI> types = faultEventType.getTypes();
        if(types == null) {
            types = new HashSet<>();
            faultEventType.setTypes(types);
        }
        types.add(COMPLEX_TYPE);
        faultEventType.setName(failureMode);
        faultEventType.setCriticality(Stream.of(criticality).collect(Collectors.toSet()));
        Function f = regFun.get(function);
        if(f == null){
            f = new Function();
            f.setName(function);
            i.addFunction(f);
            regFun.put(function, f);
        }

        FailureMode fm = new FailureMode();
        fm.setName(failureMode);
        fm.addImpairedBehavior(f);
        i.addFailureMode(fm);
        fm.addManifestation(faultEventType);
        faultEventType.setBehavior(fm);

        FailureRateRequirement frr = new FailureRateRequirement();
        frr.setUpperBound(requiredProbability);
        FailureRate fr = new FailureRate();
        fr.setRequirement(frr);
        faultEventType.setFailureRate(fr);

        return faultEventType;
    }

    protected IndependentSNSItem reusableSystem(String partNumber, String label, String stock, Double predictedFailreRate){

        if(partNumber == null)
            return null;
        String reusableSystemKey = partNumber.isEmpty() ? label : partNumber;

        IndependentSNSItem c = regSys.get(reusableSystemKey);
        if(c == null){
            c = new IndependentSNSItem();
            c.setPartNumber(partNumber);
            c.setStock(stock);
            c.setName(label);
            c.setTypeCategory(TypeCategory.KIND);
            regSys.put(reusableSystemKey, c);

            FaultEventType faultEventType = new FaultEventType();
            faultEventType.setName(String.format("%s fails", partNumber ));
            Function f = new Function();
            f.setName(String.format("%s function", partNumber ));
            FailureMode fm = new FailureMode();
            fm.setName(String.format("%s failure mode", partNumber ));
            fm.addImpairedBehavior(f);
            c.addFunction(f);
            c.addFailureMode(fm);
            fm.addManifestation(faultEventType);
            faultEventType.setBehavior(fm);

            FailureRateEstimate fre = new FailureRateEstimate();
            fre.setEstimationMethod(predictionEstimationMethod);
            fre.setValue(predictedFailreRate);
            FailureRate fr = new FailureRate();
            fr.setPrediction(fre);
            faultEventType.setFailureRate(fr);

        }
        return c;
    }


    protected List<VerificationMethod> verificationMethods(){
        List<VerificationMethod> vms = new ArrayList<>();
        vms.add(verificationMethod("Designa Appraisal"));
        vms.add(verificationMethod("Installation Appraisal"));
        vms.add(verificationMethod("Similarity Analysis"));
        vms.add(verificationMethod("Service Experience"));
        vms.add(verificationMethod("Qualitative FTA"));
        vms.add(verificationMethod("Functional FMEA"));
        vms.add(verificationMethod("Common Cause Analysis"));
        vms.add(verificationMethod("Quantitative FTA"));
        vms.add(verificationMethod("Quantitative FMEA"));
        vms.add(verificationMethod("Single Failure Mode Analysis"));
        return vms;
    }

    protected VerificationMethod verificationMethod(String label){
        return verificationMethod(label, label.toLowerCase().replaceAll("\\s+", "-"));
    }
    protected VerificationMethod verificationMethod(String label, String code){
        VerificationMethod vm = new VerificationMethod();
        vm.setUri(URI.create(Vocabulary.s_c_verification_method + "/" + code));
        vm.setName(label);
        vm.setCode(code);
        return vm;
    }

    protected System aircraft(String type){
        System c = regAc.get(type);
        if(c == null){
            c = new System();
            c.setName(type);

        }
        return c;
    }


}
