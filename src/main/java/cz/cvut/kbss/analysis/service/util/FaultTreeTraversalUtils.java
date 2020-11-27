package cz.cvut.kbss.analysis.service.util;

import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.FaultTree;
import cz.cvut.kbss.analysis.model.util.EventType;
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

    public static List<FaultEvent> rootToLeafPath(FaultTree tree, URI leafEventUri) {
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
