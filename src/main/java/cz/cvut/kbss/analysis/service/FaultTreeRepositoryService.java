package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FaultEventDao;
import cz.cvut.kbss.analysis.dao.FaultTreeDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.exception.LogicViolationException;
import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.model.util.EventType;
import cz.cvut.kbss.analysis.service.util.Pair;
import cz.cvut.kbss.analysis.service.validation.FaultEventValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FaultTreeRepositoryService {

    private final FaultTreeDao faultTreeDao;
    private final FaultEventDao faultEventDao;
    private final FaultEventValidator faultEventValidator;
    private final FaultEventRepositoryService faultEventRepositoryService;

    @Transactional(readOnly = true)
    public List<FaultTree> findAll() {
        return faultTreeDao.findAll();
    }

    @Transactional
    public FaultTree find(URI faultTreeUri) {
        log.info("> find - {}", faultTreeUri);

        FaultTree faultTree = getFaultTree(faultTreeUri);

        log.debug("Propagating probabilities through the tree");
        propagateProbabilities(faultTree);

        faultTreeDao.update(faultTree);

        return faultTree;
    }

    @Transactional
    public FaultTree create(FaultTree faultTree) {
        log.info("> create - {}", faultTree);

        faultEventValidator.validateTypes(faultTree.getManifestingEvent());

        URI faultEventUri = faultTree.getManifestingEvent().getUri();
        if (faultEventUri != null) {
            log.info("Reusing fault event - {}", faultEventUri);
            FaultEvent faultEvent = faultEventDao
                    .find(faultEventUri)
                    .orElseThrow(() -> new LogicViolationException("Fault Event is reused but does not exists in database!"));
            faultTree.setManifestingEvent(faultEvent);
        }
        faultTreeDao.persist(faultTree);

        log.info("< create - {}", faultTree);
        return faultTree;
    }

    @Transactional
    public FaultTree update(FaultTree faultTree) {
        log.info("> update - {}", faultTree);

        propagateProbabilities(faultTree);

        faultTreeDao.update(faultTree);

        log.info("< update - {}", faultTree);
        return faultTree;
    }

    @Transactional
    public void delete(URI treeUri) {
        faultTreeDao.remove(treeUri);
    }

    private void propagateProbabilities(FaultTree faultTree) {
        log.info("> propagateProbabilities - {}", faultTree);

        Double recomputedProbability = faultEventRepositoryService.propagateProbability(faultTree.getManifestingEvent());

        log.info("< propagateProbabilities - {}", recomputedProbability);
    }

    @Transactional
    public FailureModesTable createFailureModesTable(URI faultTreeUri, FailureModesTable failureModesTable) {
        log.info("> createFailureModesTable - {}, {}", faultTreeUri, failureModesTable);

        FaultTree faultTree = getFaultTree(faultTreeUri);
        faultTree.addFailureModeTable(failureModesTable);

        Set<FaultEvent> leafEvents = getLeafEvents(faultTree.getManifestingEvent());
        Set<FailureModesRow> failureModesRows = leafEvents.stream().map(leaf -> {
            FailureModesRow row = new FailureModesRow();
            row.setLocalEffect(leaf);
            return row;
        }).collect(Collectors.toSet());

        failureModesTable.setRows(failureModesRows);

        faultTreeDao.update(faultTree);

        log.info("< createFailureModesTable - {}", failureModesTable);
        return failureModesTable;
    }

    private Set<FaultEvent> getLeafEvents(FaultEvent event) {
        Set<FaultEvent> leafNodes = new HashSet<>();

        if (event.getChildren().isEmpty()) {
            leafNodes.add(event);
        } else {
            for (FaultEvent child : event.getChildren()) {
                leafNodes.addAll(getLeafEvents(child));
            }
        }
        return leafNodes;
    }

    private FaultTree getFaultTree(URI faultTreeUri) {
        return faultTreeDao
                .find(faultTreeUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find fault tree"));
    }
}
