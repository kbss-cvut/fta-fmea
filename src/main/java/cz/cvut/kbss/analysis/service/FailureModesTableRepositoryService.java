package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FailureModesTableDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.dto.table.FailureModesTableDataDTO;
import cz.cvut.kbss.analysis.dto.table.FailureModesTableField;
import cz.cvut.kbss.analysis.dto.update.FailureModesTableUpdateDTO;
import cz.cvut.kbss.analysis.model.FailureModesTable;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.FaultTree;
import cz.cvut.kbss.analysis.model.RiskPriorityNumber;
import cz.cvut.kbss.analysis.model.util.EventType;
import cz.cvut.kbss.analysis.service.util.Pair;
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
public class FailureModesTableRepositoryService extends BaseRepositoryService<FailureModesTable> {

    private final FailureModesTableDao failureModesTableDao;

    @Override
    protected GenericDao<FailureModesTable> getPrimaryDao() {
        return failureModesTableDao;
    }

    @Transactional(readOnly = true)
    public List<FailureModesTable> findAll() {
        return failureModesTableDao.findAll();
    }

    @Transactional
    public FailureModesTable updateByDTO(FailureModesTableUpdateDTO updateDTO) {
        log.info("> updateByDTO - {}", updateDTO);

        FailureModesTable table = findRequired(updateDTO.getUri());
        updateDTO.copyToEntity(table);
        update(table);

        log.info("< updateByDTO - {}", table);
        return table;
    }

    @Transactional(readOnly = true)
    public FailureModesTableDataDTO computeTableData(URI tableUri) {
        log.info("> computeTableDate - {}", tableUri);

        FailureModesTable table = findRequired(tableUri);

        FailureModesTableDataDTO tableData = new FailureModesTableDataDTO(table.getName());
        List<FailureModesTableField> columns = new ArrayList<>();

        columns.add(new FailureModesTableField("component", "Component"));
        columns.add(new FailureModesTableField("func", "Function"));
        columns.add(new FailureModesTableField("failureMode", "Failure Mode"));
        columns.add(new FailureModesTableField("localEffect", "Local Effect"));
        columns.add(new FailureModesTableField("finalEffect", "Final Effect"));
        columns.add(new FailureModesTableField("severity", "S"));
        columns.add(new FailureModesTableField("occurrence", "O"));
        columns.add(new FailureModesTableField("detection", "D"));
        columns.add(new FailureModesTableField("rpn", "S * O * D"));

        tableData.setColumns(columns);

        FaultEvent treeRoot = table.getFaultTree().getManifestingEvent();
        List<Map<String, Object>> rows = table.getRows().stream().map(r -> {
            Map<String, Object> row = new HashMap<>();
            row.put("id", r.getUri().toString());
            row.put("localEffect", r.getLocalEffect().getName());
            row.put("finalEffect", treeRoot.getName());

            RiskPriorityNumber rootRPN = treeRoot.getRiskPriorityNumber();
            row.put("severity", rootRPN.getSeverity());
            row.put("occurrence", rootRPN.getOccurrence());
            row.put("detection", rootRPN.getDetection());
            if (rootRPN.getSeverity() != null && rootRPN.getOccurrence() != null && rootRPN.getDetection() != null) {
                row.put("rpn", rootRPN.getSeverity() * rootRPN.getOccurrence() * rootRPN.getDetection());
            }

            return row;
        }).collect(Collectors.toList());

        tableData.setRows(rows);

        log.info("< computeTableDate - {}", tableData);
        return tableData;
    }

    private List<FaultEvent> leafToRootPath(FaultTree tree, URI leafEventUri) {
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
