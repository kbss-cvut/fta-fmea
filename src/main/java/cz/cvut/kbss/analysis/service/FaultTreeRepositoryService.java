package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FaultTreeDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.model.util.EventType;
import cz.cvut.kbss.analysis.model.util.GateType;
import cz.cvut.kbss.analysis.service.util.FaultTreeTraversalUtils;
import cz.cvut.kbss.analysis.util.Vocabulary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.lang.System;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FaultTreeRepositoryService extends BaseRepositoryService<FaultTree> {

    private final FaultTreeDao faultTreeDao;
    private final FaultEventRepositoryService faultEventRepositoryService;
    private final FunctionRepositoryService functionRepositoryService;
    private final FailureModeRepositoryService failureModeRepositoryService;
    private final IdentifierService identifierService;

    @Autowired
    public FaultTreeRepositoryService(@Qualifier("defaultValidator") Validator validator,
                                      FaultTreeDao faultTreeDao,
                                      FaultEventRepositoryService faultEventRepositoryService,
                                      FunctionRepositoryService functionRepositoryService,
                                      FailureModeRepositoryService failureModeRepositoryService,
                                      IdentifierService identifierService
    ) {
        super(validator);
        this.faultTreeDao = faultTreeDao;
        this.faultEventRepositoryService = faultEventRepositoryService;
        this.functionRepositoryService = functionRepositoryService;
        this.failureModeRepositoryService = failureModeRepositoryService;
        this.identifierService = identifierService;
    }

    @Override
    protected GenericDao<FaultTree> getPrimaryDao() {
        return faultTreeDao;
    }

    @Transactional
    public FaultTree findWithPropagation(URI faultTreeUri) {
        log.info("> findWithPropagation - {}", faultTreeUri);

        FaultTree faultTree = findRequired(faultTreeUri);

        log.debug("Propagating probabilities through the tree");
        propagateProbabilities(faultTree);

        update(faultTree);

        return faultTree;
    }

    @Override
    protected void prePersist(FaultTree instance) {
        super.prePersist(instance);

        URI faultEventUri = instance.getManifestingEvent().getUri();
        if (faultEventUri != null) {
            log.info("Reusing fault event - {}", faultEventUri);
            FaultEvent faultEvent = faultEventRepositoryService.findRequired(faultEventUri);
            instance.setManifestingEvent(faultEvent);
        }
    }

    @Override
    protected void preUpdate(FaultTree instance) {
        super.preUpdate(instance);
        propagateProbabilities(instance);
    }

    private void propagateProbabilities(FaultTree faultTree) {
        log.info("> propagateProbabilities - {}", faultTree);

        Double recomputedProbability = faultEventRepositoryService.propagateProbability(faultTree.getManifestingEvent());

        log.info("< propagateProbabilities - {}", recomputedProbability);
    }

    @Transactional(readOnly = true)
    public List<FaultEvent> getReusableEvents(URI faultTreeUri) {
        log.info("> getReusableEvents - {}", faultTreeUri);

        FaultTree faultTree = findRequired(faultTreeUri);
        List<FaultEvent> allEvents = faultEventRepositoryService.findAll();

        List<FaultEvent> treeEvents = getTreeEvents(faultTree);
        allEvents.removeAll(treeEvents);

        log.info("< getReusableEvents");
        return allEvents;
    }

    @Transactional(readOnly = true)
    public List<List<FaultEvent>> getTreePaths(URI faultTreeUri) {
        log.info("> getTreePaths - {}", faultTreeUri);

        FaultTree faultTree = findRequired(faultTreeUri);

        Set<FaultEvent> leafEvents = FaultTreeTraversalUtils.getLeafEvents(faultTree.getManifestingEvent());

        List<List<FaultEvent>> treePaths = leafEvents.parallelStream()
                .map(leaf -> FaultTreeTraversalUtils.rootToLeafPath(faultTree.getManifestingEvent(), leaf.getUri()))
                .collect(Collectors.toList());

        log.info("< getTreePaths - {}", treePaths);
        return treePaths;
    }

    @Transactional(readOnly = true)
    public List<List<FaultEvent>> getTreePathsAggregate() {
        log.info("> getTreePathsAggregate");

        List<FaultTree> trees = findAll();
        List<List<FaultEvent>> treePathsAggregate = trees.stream()
                .filter(t -> !faultEventRepositoryService.isRootEventReused(t.getManifestingEvent()))
                .map(t -> getTreePaths(t.getUri()))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        log.info("< getTreePathsAggregate");
        return treePathsAggregate;
    }

    @Transactional
    public FailureModesTable createFailureModesTable(URI faultTreeUri, FailureModesTable failureModesTable) {
        log.info("> createFailureModesTable - {}, {}", faultTreeUri, failureModesTable);

        FaultTree faultTree = findRequired(faultTreeUri);
        faultTree.setFailureModesTable(failureModesTable);
        failureModesTable.setFaultTree(faultTree);

        update(faultTree);

        log.info("< createFailureModesTable - {}", failureModesTable);
        return failureModesTable;
    }

    @Transactional(readOnly = true)
    public FailureModesTable getFailureModesTable(URI faultTreeUri) {
        return findRequired(faultTreeUri).getFailureModesTable();
    }

    private List<FaultEvent> getTreeEvents(FaultTree tree) {
        List<FaultEvent> treeEvents = new ArrayList<>();
        getTreeEventsRecursive(tree.getManifestingEvent(), treeEvents);
        return treeEvents;
    }

    private void getTreeEventsRecursive(FaultEvent node, List<FaultEvent> eventList) {
        if (node != null) {
            eventList.add(node);
            node.getChildren()
                    .forEach(child -> getTreeEventsRecursive(child, eventList));
        }
    }

    @Transactional
    public FaultTree generateFunctionDependencyTree(URI functionUri, String faultTreeName){
        Function function = functionRepositoryService.findRequired(functionUri);
        FaultEvent faultEvent = transferFunctionToFaultEvent(function);

        FaultTree faultTree = new FaultTree();
        faultTree.setName(faultTreeName);
        faultTree.setManifestingEvent(faultEvent);

        processRequiredFunctions(function,faultEvent);
        persist(faultTree);
        return faultTree;
    }

    private void processRequiredFunctions(Function function,FaultEvent faultEvent){
        if(!function.getRequiredFunctions().isEmpty()){
            Set<FaultEvent> faultEvents = new LinkedHashSet<>();
            for(Function f: function.getRequiredFunctions()){
                FaultEvent tmp = transferFunctionToFaultEvent(f);
                faultEvents.add(tmp);
                processRequiredFunctions(f,tmp);
            }
            faultEvent.setChildren(faultEvents);
        }else{
            faultEvent.setEventType(EventType.BASIC);
        }
    }

    private FaultEvent transferFunctionToFaultEvent(Function functionToTransfer){
        String functionUri = functionToTransfer.getUri().toString();
        URI faultEventUri = identifierService.composeIdentifier(Vocabulary.s_c_FaultEvent,functionUri.substring(functionUri.lastIndexOf("/") + 1));

        if(faultEventRepositoryService.exists(faultEventUri)){
            return faultEventRepositoryService.findRequired(faultEventUri);
        }else{
            FaultEvent faultEvent = new FaultEvent();
            faultEvent.setUri(faultEventUri);
            faultEvent.setName(functionToTransfer.getName() + " failure");
            faultEvent.setEventType(EventType.INTERMEDIATE);
            faultEvent.setGateType(GateType.OR);

            FailureMode failureMode = new FailureMode();
            failureMode.setName(functionToTransfer.getName() + " failure mode");
            failureMode.setComponent(functionRepositoryService.getComponent(functionToTransfer.getUri()));
            failureMode.setFunctions(functionToTransfer.getRequiredFunctions());
            faultEvent.setFailureMode(failureMode);

            failureModeRepositoryService.persist(failureMode);
            faultEventRepositoryService.persist(faultEvent);
            return faultEvent;
        }
    }

}
