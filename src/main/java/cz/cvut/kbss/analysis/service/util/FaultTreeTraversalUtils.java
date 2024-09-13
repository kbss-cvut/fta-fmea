package cz.cvut.kbss.analysis.service.util;

import cz.cvut.kbss.analysis.exception.LogicViolationException;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.fta.FtaEventType;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.*;

@Slf4j
public class FaultTreeTraversalUtils {

    public static Set<FaultEvent> getLeafEvents(FaultEvent event) {
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

    public static List<FaultEvent> rootToLeafPath(FaultEvent rootEvent, URI leafEventUri) {
        log.info("> rootToLeafEventPath - {}, {}", rootEvent, leafEventUri);

        Set<FaultEvent> visited = new HashSet<>();
        LinkedList<Pair<FaultEvent, List<FaultEvent>>> queue = new LinkedList<>();

        List<FaultEvent> startList = new ArrayList<>();
        startList.add(rootEvent);

        queue.push(Pair.of(rootEvent, startList));

        while (!queue.isEmpty()) {
            Pair<FaultEvent, List<FaultEvent>> pair = queue.pop();
            FaultEvent currentEvent = pair.getFirst();
            List<FaultEvent> path = pair.getSecond();
            visited.add(currentEvent);

            for (FaultEvent child : currentEvent.getChildren()) {
                if (child.getUri().equals(leafEventUri)) {
                    if (child.getEventType() == FtaEventType.INTERMEDIATE) {
                        String message = "Intermediate event must not be the end of the path!";
                        log.warn(message);
                        throw new LogicViolationException("error.faultTree.intermediateEventAsLeaf",message);
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
