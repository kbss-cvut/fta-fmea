package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FaultTreeDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
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
public class FaultTreeRepositoryService extends BaseRepositoryService<FaultTree> {

    private final FaultTreeDao faultTreeDao;
    private final FaultEventValidator faultEventValidator;
    private final FaultEventRepositoryService faultEventRepositoryService;

    @Override
    protected GenericDao<FaultTree> getPrimaryDao() {
        return faultTreeDao;
    }

    @Transactional
    public FaultTree findWithPropagation(URI faultTreeUri) {
        log.info("> find - {}", faultTreeUri);

        FaultTree faultTree = findRequired(faultTreeUri);

        log.debug("Propagating probabilities through the tree");
        propagateProbabilities(faultTree);

        update(faultTree);

        return faultTree;
    }

    @Override
    protected void prePersist(FaultTree instance) {
        faultEventValidator.validateTypes(instance.getManifestingEvent());

        URI faultEventUri = instance.getManifestingEvent().getUri();
        if (faultEventUri != null) {
            log.info("Reusing fault event - {}", faultEventUri);
            FaultEvent faultEvent = faultEventRepositoryService.findRequired(faultEventUri);
            instance.setManifestingEvent(faultEvent);
        }
    }

    @Override
    protected void preUpdate(FaultTree instance) {
        propagateProbabilities(instance);
    }

    private void propagateProbabilities(FaultTree faultTree) {
        log.info("> propagateProbabilities - {}", faultTree);

        Double recomputedProbability = faultEventRepositoryService.propagateProbability(faultTree.getManifestingEvent());

        log.info("< propagateProbabilities - {}", recomputedProbability);
    }

    @Transactional(readOnly = true)
    public List<List<FaultEvent>> getTreePaths(URI faultTreeUri) {
        log.info("> exploreTreePaths - {}", faultTreeUri);

        FaultTree faultTree = findRequired(faultTreeUri);

        Set<FaultEvent> leafEvents = getLeafEvents(faultTree.getManifestingEvent());

        List<List<FaultEvent>> treePaths = leafEvents.parallelStream()
                .map(leaf -> rootToLeafPath(faultTree, leaf.getUri()))
                .collect(Collectors.toList());

        log.info("< exploreTreePaths - {}", treePaths);
        return treePaths;
    }

    @Transactional
    public FailureModesTable createFailureModesTable(URI faultTreeUri, FailureModesTable failureModesTable) {
        log.info("> createFailureModesTable - {}, {}", faultTreeUri, failureModesTable);

        FaultTree faultTree = findRequired(faultTreeUri);
        faultTree.addFailureModeTable(failureModesTable);

        Set<FaultEvent> leafEvents = getLeafEvents(faultTree.getManifestingEvent());
        Set<FailureModesRow> failureModesRows = leafEvents.stream().map(leaf -> {
            FailureModesRow row = new FailureModesRow();
            row.setLocalEffect(leaf);
            return row;
        }).collect(Collectors.toSet());

        failureModesTable.setRows(failureModesRows);

        update(faultTree);

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

    private List<FaultEvent> rootToLeafPath(FaultTree tree, URI leafEventUri) {
        log.info("> rootToLeafEventPath - {}, {}", tree, leafEventUri);

        Set<FaultEvent> visited = new HashSet<>();
        LinkedList<Pair<FaultEvent, List<FaultEvent>>> queue = new LinkedList<>();

        FaultEvent startEvent = tree.getManifestingEvent();
        List<FaultEvent> startList = new ArrayList<>();
        startList.add(startEvent);

        queue.push(Pair.of(startEvent, startList));

        while (!queue.isEmpty()) {
            Pair<FaultEvent, List<FaultEvent>> pair = queue.pop();
            FaultEvent currentEvent = pair.getFirst();
            List<FaultEvent> path = pair.getSecond();
            visited.add(currentEvent);

            for (FaultEvent child : currentEvent.getChildren()) {
                if (child.getUri().equals(leafEventUri)) {
                    if (child.getEventType() == EventType.INTERMEDIATE) {
                        log.warn("Intermediate event must not be the end of the path!");
                        return new ArrayList<>();
                    }

                    path.add(child);
                    Collections.reverse(path);
                    return path;
                } else {
                    if (!visited.contains(child)) {
                        visited.add(child);
                        List<FaultEvent> newPath = new ArrayList<>(path);
                        newPath.add(child);
                        queue.push(Pair.of(child, newPath));
                    }
                }
            }
        }

        log.warn("< rootToLeafEventPath - failed to find path from root to leaf");
        return new ArrayList<>();
    }

}
