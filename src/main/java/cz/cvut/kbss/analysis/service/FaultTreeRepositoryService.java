package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FaultEventDao;
import cz.cvut.kbss.analysis.dao.FaultTreeDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.exception.LogicViolationException;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.FaultTree;
import cz.cvut.kbss.analysis.model.User;
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

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FaultTreeRepositoryService {

    private final FaultTreeDao faultTreeDao;
    private final FaultEventDao faultEventDao;
    private final FaultEventValidator faultEventValidator;
    private final FaultEventRepositoryService faultEventRepositoryService;

    @Transactional(readOnly = true)
    public List<FaultTree> findAllForUser(User user) {
        return faultTreeDao.findAllForUser(user);
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

        faultEventValidator.validate(faultTree.getManifestingEvent());

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

    @Transactional(readOnly = true)
    public List<FaultEvent> rootToLeafEventPath(URI treeUri, URI leafEventUri) {
        log.info("> rootToLeafEventPath - {}, {}", treeUri, leafEventUri);

        FaultTree tree = getFaultTree(treeUri);

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
                        String message = "Intermediate event must not be the end of the path!";
                        log.warn(message);
                        throw new LogicViolationException(message);
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

    private FaultTree getFaultTree(URI faultTreeUri) {
        return faultTreeDao
                .find(faultTreeUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find fault tree"));
    }
}
