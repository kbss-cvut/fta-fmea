package cz.cvut.kbss.analysis.service;

import com.opencsv.CSVWriter;
import cz.cvut.kbss.analysis.dao.FailureModesTableDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.dto.table.FailureModesTableDataDTO;
import cz.cvut.kbss.analysis.dto.table.FailureModesTableField;
import cz.cvut.kbss.analysis.dto.update.FailureModesTableUpdateDTO;
import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.service.util.FaultTreeTraversalUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringWriter;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FailureModesTableRepositoryService extends BaseRepositoryService<FailureModesTable> {

    private final FailureModesTableDao failureModesTableDao;
    private final FaultEventRepositoryService faultEventRepositoryService;

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
        columns.add(new FailureModesTableField("function", "Function"));
        columns.add(new FailureModesTableField("failureMode", "Failure Mode"));
        columns.add(new FailureModesTableField("localEffect", "Local Effect"));

        List<Map<String, Object>> rows = computeTableRows(table, columns);

        columns.add(new FailureModesTableField("finalEffect", "Final Effect"));
        columns.add(new FailureModesTableField("severity", "S"));
        columns.add(new FailureModesTableField("occurrence", "O"));
        columns.add(new FailureModesTableField("detection", "D"));
        columns.add(new FailureModesTableField("rpn", "RPN"));
        columns.add(new FailureModesTableField("mitigation", "Mitigation"));

        tableData.setColumns(columns);
        tableData.setRows(rows);

        log.info("< computeTableDate - {}", tableData);
        return tableData;
    }

    private List<Map<String, Object>> computeTableRows(FailureModesTable table, List<FailureModesTableField> columns) {
        log.info("> computeTableRows");

        FaultTree tree = table.getFaultTree();
        FaultEvent treeRoot = tree.getManifestingEvent();

        int maxEffects = table.getRows().stream()
                .mapToInt(row -> row.getEffects().size())
                .max()
                .orElse(1); // one for root

        for (int i = 0; i < (maxEffects - 1); i++) {
            columns.add(new FailureModesTableField("nextEffect-" + i, "Next Effect"));
        }

        List<List<Map<String, Object>>> rowLists = table.getRows().stream().map(r -> {
            Map<String, Object> row = new HashMap<>();

            row.put("id", r.getUri().toString());
            row.put("rowId", r.getUri().toString());

            FaultEvent localEffect = faultEventRepositoryService.findRequired(r.getLocalEffect());
            row.put("localEffect", localEffect.getName());

            List<FaultEvent> nextEffectsList = FaultTreeTraversalUtils
                    .rootToLeafPath(tree, localEffect.getUri())
                    .stream()
                    .filter(e -> !e.getUri().equals(treeRoot.getUri()))
                    .filter(e -> r.getEffects().contains(e.getUri()))
                    .collect(Collectors.toList());

            for (int i = 0; i < nextEffectsList.size(); i++) {
                row.put("nextEffect-" + i, nextEffectsList.get(i).getName());
            }

            row.put("finalEffect", treeRoot.getName());

            RiskPriorityNumber rpn = r.getRiskPriorityNumber();
            row.put("severity", rpn.getSeverity());
            row.put("occurrence", rpn.getOccurrence());
            row.put("detection", rpn.getDetection());
            if (rpn.getSeverity() != null && rpn.getOccurrence() != null && rpn.getDetection() != null) {
                row.put("rpn", rpn.getSeverity() * rpn.getOccurrence() * rpn.getDetection());
            }

            FailureMode failureMode = localEffect.getFailureMode();
            if (failureMode != null) {
                row.put("failureMode", failureMode.getName());
                row.put("component", failureMode.getComponent().getName());

                if (failureMode.getMitigation() != null) {
                    row.put("mitigation", failureMode.getMitigation().getDescription());
                }

                if (!failureMode.getFunctions().isEmpty()) {
                    return failureMode.getFunctions().stream()
                            .map(function -> {
                                Map<String, Object> functionRow = new HashMap<>(row);
                                functionRow.put("id", functionRow.get("id") + function.getUri().toString());
                                functionRow.put("function", function.getName());
                                return functionRow;
                            })
                            .collect(Collectors.toList());
                }

            }

            List<Map<String, Object>> resultList = new ArrayList<>();
            resultList.add(row);
            return resultList;
        }).collect(Collectors.toList());

        return rowLists.stream().flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public String export(URI tableUri) {
        log.info("> export - {}", tableUri);

        FailureModesTableDataDTO tableData = computeTableData(tableUri);

        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);

        String[] headerTitles = tableData.getColumns().stream().map(FailureModesTableField::getHeaderName).toArray(String[]::new);
        csvWriter.writeNext(headerTitles);

        String[] headerFields = tableData.getColumns().stream().map(FailureModesTableField::getField).toArray(String[]::new);
        tableData.getRows().forEach(row -> {
            String[] rowValues = new String[headerFields.length];

            for (int i = 0; i < headerFields.length; i++) {
                rowValues[i] = Objects.toString(row.get(headerFields[i]), null);
            }

            csvWriter.writeNext(rowValues);
        });

        log.info("< export");
        return stringWriter.toString();
    }

}
