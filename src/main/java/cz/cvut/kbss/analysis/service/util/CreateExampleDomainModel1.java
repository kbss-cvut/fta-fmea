package cz.cvut.kbss.analysis.service.util;

import cz.cvut.kbss.analysis.model.System;
import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.model.ava.ATASystem;
import cz.cvut.kbss.analysis.model.ava.FHAEventType;
import cz.cvut.kbss.analysis.model.ava.IndependentSNSItem;
import cz.cvut.kbss.analysis.model.ava.SNSComponent;
import cz.cvut.kbss.analysis.model.method.EstimationMethod;
import cz.cvut.kbss.analysis.model.method.VerificationMethod;
import cz.cvut.kbss.analysis.model.util.Anonymizer;
import cz.cvut.kbss.analysis.model.util.Exporter;
import cz.cvut.kbss.analysis.model.util.SensitiveProperties;
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

    public static final String ATOMIC_FUNCTION_NAME = "ATOMIC_FUNCTION_NAME";
    private final EntityManager em;

    protected String acModel;
    private Map<String, IndependentSNSItem> regSys = new HashMap<>();
    private Map<String, System> regAc = new HashMap<>();
    private Map<String, SNSComponent> regSns = new HashMap<>();
    private Map<SNSComponent, String> rregSns = new HashMap<>();
    private Map<String, ATASystem> regAta = new HashMap<>();
    private Map<ATASystem, String> rregAta = new HashMap<>();
    private Map<String, Function> regFun = new HashMap<>();
    private Map<String, FailureMode> regFM = new HashMap<>();
    private List<VerificationMethod> verificationMethods = verificationMethods();


    private EstimationMethod predictionEstimationMethod;
    private EstimationMethod estimateEstimationMethod;


    private SensitiveProperties sensitiveProperties;

    private Anonymizer anonymizer = new Anonymizer();

    public CreateExampleDomainModel1(EntityManager em) {
        this.em = em;
        predictionEstimationMethod = new EstimationMethod();
        predictionEstimationMethod.setName("prediction");
        estimateEstimationMethod = new EstimationMethod();
        estimateEstimationMethod.setName("estimate");
    }

    public void reset(){
//        regSys = new HashMap<>();
        regAc = new HashMap<>();
        regSns = new HashMap<>();
        rregSns = new HashMap<>();
//        regAta = new HashMap<>();
//        rregAta = new HashMap<>();
        regFun = new HashMap<>();
        regFM = new HashMap<>();
        sensitiveProperties = new SensitiveProperties();
//        verificationMethods = verificationMethods();
    }


    @Transactional
    public List<FaultEventType> createFHABasedFailureRateEstimates() {
        CreateExampleFHABasedFailureRateEstimates exampleGenerator = new CreateExampleFHABasedFailureRateEstimates(em);
        List<FaultEventType> ret = exampleGenerator.generateFHABasedFailureRateEstimates((d) -> failureRate(d, estimateEstimationMethod));
        return ret;
    }

    @Transactional
    public List<? extends Item> createModel(){
//        log.info("post construct - CreateExampleDomainModel1.createModel, em = {}", em);
        System ac1 = acPartonomy1("acm1");
        persist(ac1);
        System ac2 = acPartonomy1("acm2");
        persist(ac2);
        return Stream.of(ac1, ac2).toList();
    }

    @Transactional
    public void export(String file){
        Exporter exporter = new Exporter();
        exporter.exportAsTrig(em, file);
    }

    @Transactional
    public void anonymize(){
        anonymizer.anonymize(em, sensitiveProperties);
    }



//    @Override
//    public void onApplicationEvent(ContextRefreshedEvent event) {
//
//        log.info("post construct - CreateExampleDomainModel1.createModel, em = {}", em);
//        System ac = acPartonomy1();
//        EntityManager em = event.getApplicationContext().getBean(EntityManager.class);
//        persist(ac, em);
//    }

//    @Transactional
    public void persist(Item ac){
        // persist ata systems
        Descriptor d = new EntityDescriptor(URI.create("http://context-with-general-ATA-SNS-partonomy"));
        for(ATASystem s : regAta.values())
            _persist(s, d);
        // persist independent items
        d = new EntityDescriptor(URI.create("http://context-with-independent-items"));
        for(IndependentSNSItem s : regSys.values())
            _persist(s, d);

        d = new EntityDescriptor(URI.create("http://context-with-ata-sns-partonomy-of--" + acModel));
        _persist(ac, d);
    }

    public void _persist(Item item, Descriptor d){


        Stream<Item> itemStream = depthFirst(item);
//        itemStream.forEach(i -> java.lang.System.out.println(i.getName()));
        List<Item> items = itemStream.collect(Collectors.toList());

//        // persist item types
//        for(Item it : items){
//            if(it.getSupertypes() != null){
//                for (Item type : it.getSupertypes()) {
//                    _persist(type, d);
//                }
//            }
//        }

        for (Item i : items) {
            if(i.getUri() == null) {
                persist(i, d);
//                em.persist(i, d);
            }
        }

        // persist behavioral  manifestations
        for(Item it : items){

            List<Behavior> behaviors = Stream.of(it.getFailureModes(), it.getFunctions())
                    .filter(s -> s != null)
                    .flatMap(s -> s.stream()).collect(Collectors.toList());

            for(Behavior b : behaviors){
                persist(b,d);
                if(b.getManifestations() != null && !b.getManifestations().isEmpty())
                    for(Event e : b.getManifestations()) {
                        persist(e, d);
//                        em.persist(e, d);
                    }
//                else
//                    persist(b,d);
//                em.persist(b, d);
            }
        }
//
//
//
//
//        Item i = items.get(0);
//        em.persist(, d);

    }

    protected void persist(NamedEntity e, Descriptor d) {

        if(!em.contains(e)){
            if (e.getUri() != null) {
                e = em.merge(e);
            } else {
                em.persist(e, d);
            }
        }
//        if (e.getUri() != null) {
//            if (!em.contains(e))
//                try {
//                    em.merge(e);
//                } catch (OWLEntityExistsException ex) {
//                    log.error("Exception merging Entity {}:{}<{}>", e.getName(), e.getClass().getSimpleName(), e.getUri(), ex);
//                }
//        } else {
//            em.persist(e, d);
//        }

//        if((e.getUri() != null && em.find(e.getClass(), e.getUri()) != null))
//            if(!em.contains(e))
//                em.merge(e);
//            else
//                em.persist(e, d);
    }

    public void persistWithTransaction(System ac, EntityManager em ){
        Descriptor d = new EntityDescriptor(URI.create("http://context-with-sns-partonomy-of--" + acModel));

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

//    public System acPartonomy1(){
//
//    }
    public System acPartonomy1(String acModel){
        reset();
        this.acModel = acModel;
//        String acModel = "acm1";
        Pair<String, SNSComponent> s21 = snsComponent(acModel, "Environmental control", "21-00-00", null, null, null, null, null);
        Pair<String, SNSComponent> s21_10 = snsComponent(s21.getFirst(), s21.getSecond(), "Compression", "21-10-00", null, null, null, null, null);
        Pair<String, SNSComponent> s21_10_01 = snsComponent(s21_10.getFirst(), s21_10.getSecond(), "Expansion joint", "21-10-01", "R92.820", 2, "M4114785", Arrays.asList("Ca2", "Ca20"), null);
        atomicFaultEventType(s21_10_01.getFirst(), s21_10_01.getSecond(), "Expansion joint","21-10-01", "R92.820");
        Pair<String, SNSComponent> s21_10_02 = snsComponent(s21_10.getFirst(), s21_10.getSecond(), "Air flow limiter", "21-10-02", "44200.09", 1, "M5155470", Arrays.asList("Ca14"), 1./200000);
        atomicFaultEventType(s21_10_02.getFirst(), s21_10_02.getSecond(), "Air flow limiter", "21-10-02", "44200.09");
        Pair<String, SNSComponent> s21_10_03 = snsComponent(s21_10.getFirst(), s21_10.getSecond(), "Filter", "21-10-03", "AO-2016/AV", 1, "M5102422", Arrays.asList("Ca15"), 1./10000);
        atomicFaultEventType(s21_10_03.getFirst(), s21_10_03.getSecond(), "Filter", "21-10-03", "AO-2016/AV");
        Pair<String, SNSComponent> s21_10_03_c1 = snsComponent(s21_10_03.getFirst(), s21_10_03.getSecond(), "Filter element", null, "K2016-AO/AV", 1, null, Arrays.asList("2a"), null);
        atomicFaultEventType(s21_10_03_c1.getFirst(), s21_10_03_c1.getSecond(), "Filter element", null, "K2016-AO/AV");
        Pair<String, SNSComponent> s21_10_03_c1_c1 = snsComponent(s21_10_03_c1.getFirst(), s21_10_03_c1.getSecond(), "Gasket as part of filter elmnt or seprtly", null, "BVK/AV4 NG", 1, null, Arrays.asList("v TP pozice 2b"), null);
        atomicFaultEventType(s21_10_03_c1_c1.getFirst(), s21_10_03_c1_c1.getSecond(), "Gasket as part of filter elmnt or seprtly", null, "BVK/AV4 NG");
        Pair<String, SNSComponent> s21_10_03_c2 = snsComponent(s21_10_03.getFirst(), s21_10_03.getSecond(), "Gasket is not part of elmnt", null, "DAK/AV1 NG", 1, null, Arrays.asList("v TP pozice 1b"), null);
        atomicFaultEventType(s21_10_03_c2.getFirst(), s21_10_03_c2.getSecond(), "Gasket is not part of elmnt", null, "DAK/AV1 NG");
        Pair<String, SNSComponent> s21_10_03_c3 = snsComponent(s21_10_03.getFirst(), s21_10_03.getSecond(), "V-band clamp", null, "BVK/AV13 NG", 1, null, Arrays.asList("v TP pozice 1e"), null);
        atomicFaultEventType(s21_10_03_c3.getFirst(), s21_10_03_c3.getSecond(), "V-band clamp", null, "BVK/AV13 NG");
        Pair<String, SNSComponent> s21_10_03_c4 = snsComponent(s21_10_03.getFirst(), s21_10_03.getSecond(), "Washer", null, "BVK/AV8", 1, null, Arrays.asList("v TP pozice 1c"), null);
        atomicFaultEventType(s21_10_03_c4.getFirst(), s21_10_03_c4.getSecond(), "Washer", null, "BVK/AV8");
        Pair<String, SNSComponent> s21_10_03_c5 = snsComponent(s21_10_03.getFirst(), s21_10_03.getSecond(), "Nut self-locking", null, "BVK/AV30", 1, null, Arrays.asList("v TP pozice 1d"), null);
        atomicFaultEventType(s21_10_03_c5.getFirst(), s21_10_03_c5.getSecond(), "Nut self-locking", null, "BVK/AV30");
        Pair<String, SNSComponent> s21_30 = snsComponent(s21.getFirst(), s21.getSecond(), "Pressurization control", "21-30-00", null, null, null, null, null);
        Pair<String, SNSComponent> s21_30_01 = snsComponent(s21_30.getFirst(), s21_30.getSecond(), "Regulator", "21-30-01", "VP-01-039NG-AV", 1, "M5142230", Arrays.asList("Cd1"), 1./60000);
        atomicFaultEventType(s21_30_01.getFirst(), s21_30_01.getSecond(), "Regulator", "21-30-01", "VP-01-039NG-AV");
        Pair<String, SNSComponent> s21_30_02 = snsComponent(s21_30.getFirst(), s21_30.getSecond(), "Control valve", "21-30-02", "VP-02-039NG-AV", 1, "M5142232", Arrays.asList("Cd3"), 1./80000);
        atomicFaultEventType(s21_30_02.getFirst(), s21_30_02.getSecond(), "Control valve", "21-30-02", "VP-02-039NG-AV");
        Pair<String, SNSComponent> s21_30_02_c1 = snsComponent(s21_30_02.getFirst(), s21_30_02.getSecond(), "Gasket", null, "pn-13", 1, null, null, null);
        atomicFaultEventType(s21_30_02_c1.getFirst(), s21_30_02_c1.getSecond(), "Gasket", null, "pn-13");
        Pair<String, SNSComponent> s21_30_03 = snsComponent(s21_30.getFirst(), s21_30.getSecond(), "Control and safety valve", "21-30-03", "VP-03-039NG-AV", 1, "M5142234", Arrays.asList("Cd5"), 1./60000);
        atomicFaultEventType(s21_30_03.getFirst(), s21_30_03.getSecond(), "Control and safety valve", "21-30-03", "VP-03-039NG-AV");
        Pair<String, SNSComponent> s21_30_03_c1 = snsComponent(s21_30_03.getFirst(), s21_30_03.getSecond(), "Gasket", null, "pn-14", 1, null, null, null);
        atomicFaultEventType(s21_30_03_c1.getFirst(), s21_30_03_c1.getSecond(), "Gasket", null, "pn-14");

        fhaFaultEventType(s21_30.getSecond(),
                "1.1 Regulace tlaku v kabině",
                "FC_1.1. Tlak v kabině mimo regulační meze signalizovaný na MFD (CABIN PRESS/Caution)",
                0.0004, 1);
        fhaFaultEventType(s21_30.getSecond(),
                "1.1 Regulace tlaku v kabině",
                "FC_1.2. Tlak v kabině mimo krajní meze signalizovaný na MFD (CABIN PRESS/Warning) na table CANOPY CAB PRESS  - podtlak v kabině / překročena max. kabin. výška",
                0.003, 2);
        fhaFaultEventType(s21_30.getSecond(),
                "1.1 Regulace tlaku v kabině",
                "FC_1.3. Tlak v kabině mimo krajní meze signalizovaný pouze na table CANOPY CAB PRESS  - podtlak v kabině / překročena max. kabin. výška",
                0.003,3);
        fhaFaultEventType(s21_30.getSecond(),
                "1.1 Regulace tlaku v kabině",
                "FC_1.4. Tlak v kabině mimo krajní meze nesignalizovaný na MFD ani na table - podtlak v kabině / překročena max. kabin. výška",
                0.02,4);
        fhaFaultEventType(s21_30.getSecond(),
                "1.1 Regulace tlaku v kabině",
                "FC_1.5. Tlak v kabině mimo krajní meze signalizovaný na MFD (CABIN PRESS/Warning) a table CANOPY CAB a na table CANOPY CAB PRESS  - nebezpečný přetlak",
                0.003,1);
        fhaFaultEventType(s21_30.getSecond(),
                "1.1 Regulace tlaku v kabině",
                "FC_1.6. Tlak v kabině mimo krajní nesignalizovaný na  MFD, pouze na table - nebezpečný přetlak",
                0.1,2);

        System aircraft = (System)s21.getSecond().getParentComponent();
        java.lang.System.out.println(aircraft);
        return aircraft;
    }

    protected Pair<String, SNSComponent> snsComponent(String acmodel, String label, String ataCode, String partNumber, Integer quantity, String stock, Collection<String> schematicDescription, Double failureRate){
        sensitiveProperties
                .addPartNumber(partNumber)
                .addStock(stock);
        System ac = regAc.get(acmodel);
        if(ac == null){
            ac = new System();
            ac.setName(acmodel);
            ac.setComponents(new HashSet<>());
            regAc.put(acmodel, ac);
        }
        String componentKey = ataCode;

        SNSComponent c = regSns.get(componentKey);
        if(c == null){
            c = snsComponent(label, ataCode, partNumber, quantity, stock, schematicDescription, failureRate);
            regSns.put(componentKey, c);
            rregSns.put(c, componentKey);
        }
        ac.addComponent(c);

        return Pair.of(componentKey, c);
    }

    protected String entityKey(String ... path){
        return String.join("-", path);
    }

    protected String norm(String s) {
        return s != null ? s : "";
    }

    protected Pair<String, SNSComponent> snsComponent(String keyPrefix, SNSComponent parent, String label, String ataCode, String partNumber, Integer quantity, String stock, Collection<String> schematicDescription, Double failureRate){
        sensitiveProperties
                .addPartNumber(partNumber)
                .addStock(stock);
        String componentKey = ataCode != null ? ataCode : entityKey(keyPrefix, label);
        SNSComponent c = regSns.get(componentKey);
        if(c == null){
            c = snsComponent(label, componentKey, partNumber, quantity, stock, schematicDescription, failureRate);
            regSns.put(componentKey, c);
            rregSns.put(c, componentKey);
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
            regAta.put(ataCode, c);
            rregAta.put(c, ataCode);
        }
        return c;
    }
    protected FaultEventType atomicFaultEventType(String keyPrefix, Component c, String label, String ataCode, String partNumber){
        // assuming that there is only one of each function, failure mode and failure.
        String name = keyPrefix;
        if(keyPrefix.matches(".*\\d"))
            name = name + " " + label;
        String function = String.format("Function of %s ", name);
        String failure = String.format("Failure of %s", name);
        String failureMode = String.format("Failure Mode of %s", name);

        FaultEventType faultEventType = faultEventType(c, new FaultEventType(), failure, function, function, failureMode);
//        // set function supertypes
//        c.getFunctions().iterator().next().setSupertypes(new HashSet<>(
//                c.getSupertypes().stream()
//                        .map(t -> t.getFunctions().iterator().next())
//                        .collect(Collectors.toSet())
//        ));
//        // set failuremode supertypes
//        c.getFailureModes().iterator().next().setSupertypes(new HashSet<>(
//                c.getSupertypes().stream()
//                        .map(t -> t.getFailureModes().iterator().next())
//                        .collect(Collectors.toSet())
//        ));
//        // set faultevent supertypes
//        faultEventType.setSupertypes(new HashSet<>(
//                c.getFailureModes().stream()
//                        .map(b -> b.getSupertypes().iterator().next())
//                        .map(t -> t.getManifestations().iterator().next())
//                        .collect(Collectors.toSet())
//        ));

        // assuming component supertypes are set
        return faultEventType;
    }

    protected FaultEventType fhaFaultEventType(Item item, String function, String failure, Double requiredProbability,
                                               Integer criticality){


//        FHAEventType faultEventType = setUpFaultEventType(item,  new FHAEventType(), null, function, function, failureMode);
        sensitiveProperties.addFaultEventName(failure);
        FHAEventType faultEventType = faultEventType(item, new FHAEventType(), failure, function, function, "Failure Mode - " + failure);
        faultEventType.getTypes().add(COMPLEX_TYPE);

        faultEventType.setCriticality(Stream.of(criticality).collect(Collectors.toSet()));

        FailureRateRequirement frr = new FailureRateRequirement();
        frr.setUpperBound(requiredProbability);
        FailureRate fr = new FailureRate();
        fr.setRequirement(frr);
        faultEventType.setFailureRate(fr);

        return faultEventType;
    }

    protected <T extends FaultEventType> T  faultEventType(Item i, T componentFailure, String failure, String functionKey, String function, String failureMode){
        ATASystem ataRole = i.getSupertypes().stream().filter(t -> t instanceof ATASystem).map(t -> (ATASystem)t).findFirst().get();
        FaultEventType ataRoleFailure = setUpFaultEventType(ataRole, new FaultEventType(), failure, TypeCategory.ROLE, "ATA - " + functionKey, function, failureMode);
        setUpFaultEventType(i, componentFailure, failure,null, functionKey, function, failureMode);

        Set<FaultEventType> failureSupertypes = new HashSet<>();
        failureSupertypes.add(ataRoleFailure);
        i.getSupertypes().stream()
                .filter(t -> t instanceof IndependentSNSItem)
                .map(t -> (IndependentSNSItem)t).findFirst()
                .ifPresent(t -> failureSupertypes.add(
                        (FaultEventType) t.getFailureModes().iterator().next().getManifestations().iterator().next())
                );
        // compose failure supertype
        componentFailure.setSupertypes(new HashSet<>(failureSupertypes));
        // compose failure mode supertypes
        componentFailure.getBehavior().setSupertypes(
                new HashSet<>(failureSupertypes.stream().map(f -> f.getBehavior()).toList())
        );
        // compose function supertypes
        componentFailure.getBehavior().getImpairedBehaviors().iterator().next().setSupertypes(
                new HashSet<>(failureSupertypes.stream().map(f -> f.getBehavior().getImpairedBehaviors().iterator().next()).toList())
        );

        return componentFailure;
    }

    protected <T extends FaultEventType> T setUpFaultEventType(Item i, T faultEventType, String failure, TypeCategory typeCategory,  String functionKey, String function, String failureMode){
        Set<URI> types = faultEventType.getTypes();
        faultEventType.setTypeCategory(typeCategory);
        faultEventType.setName(failure);
        if(types == null) {
            types = new HashSet<>();
            faultEventType.setTypes(types);
        }

        Function f = regFun.get(functionKey);
        if(f == null){
            f = new Function();
            f.setName(function);
            i.addFunction(f);
            f.setTypeCategory(typeCategory);
            regFun.put(functionKey, f);
        }

        FailureMode fm = new FailureMode();
        fm.setTypeCategory(typeCategory);
        fm.setName(failureMode);
        fm.addImpairedBehavior(f);
        i.addFailureMode(fm);
        fm.addManifestation(faultEventType);
        faultEventType.setBehavior(fm);
        regFM.put(failure, fm);

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
            faultEventType.setTypeCategory(TypeCategory.KIND);
            faultEventType.setName(String.format("Failure of %s", partNumber ));
            Function f = new Function();
            f.setTypeCategory(TypeCategory.KIND);
            f.setName(String.format("Function of %s(%s)", label, partNumber ));
            regFun.put(f.getName(), f);
            FailureMode fm = new FailureMode();
            fm.setTypeCategory(TypeCategory.KIND);
            fm.setName(String.format("%s(%s) failure mode", label, partNumber ));
            fm.addImpairedBehavior(f);
            c.addFunction(f);
            c.addFailureMode(fm);
            regFM.put(fm.getName(), fm);
//            fm.addManifestation(faultEventType);
            faultEventType.setBehavior(fm);

            FailureRate fr = failureRate(predictedFailreRate, predictionEstimationMethod);
            faultEventType.setFailureRate(fr);
        }
        return c;
    }

    public FailureRate failureRate(Double predictedFailureRate, EstimationMethod method){
        FailureRate fr = new FailureRate();
        FailureRateEstimate fre = failureRateEstimate(predictedFailureRate, method);
        fr.setEstimate(fre);
        return fr;
    }

    public FailureRateEstimate failureRateEstimate(Double predictedFailureRate, EstimationMethod method){
        FailureRateEstimate fre = new FailureRateEstimate();
        fre.setEstimationMethod(method);
        fre.setValue(predictedFailureRate);
        return fre;
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
