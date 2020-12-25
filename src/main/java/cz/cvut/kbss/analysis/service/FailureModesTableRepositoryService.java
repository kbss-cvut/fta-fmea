package cz.cvut.kbss.analysis.service;

import com.opencsv.CSVWriter;
import cz.cvut.kbss.analysis.dao.FailureModesTableDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.dto.table.FailureModesTableDataDTO;
import cz.cvut.kbss.analysis.dto.table.FailureModesTableField;
import cz.cvut.kbss.analysis.dto.update.FailureModesTableUpdateDTO;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.model.FailureModesTable;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.RiskPriorityNumber;
import cz.cvut.kbss.analysis.service.util.FaultTreeTraversalUtils;
import cz.cvut.kbss.analysis.service.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.io.StringWriter;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
public class FailureModesTableRepositoryService extends BaseRepositoryService<FailureModesTable> {

    private final FailureModesTableDao failureModesTableDao;
    private final FaultEventRepositoryService faultEventRepositoryService;

    @Autowired
    public FailureModesTableRepositoryService(@Qualifier("defaultValidator") Validator validator, FailureModesTableDao failureModesTableDao, FaultEventRepositoryService faultEventRepositoryService) {
        super(validator);
        this.failureModesTableDao = failureModesTableDao;
        this.faultEventRepositoryService = faultEventRepositoryService;
    }

    @Override
    protected GenericDao<FailureModesTable> getPrimaryDao() {
        return failureModesTableDao;
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

        int maxEffects = table.getRows().stream()
                .mapToInt(row -> row.getEffects().size())
                .max()
                .orElse(1); // one for root

        for (int i = 0; i < (maxEffects - 1); i++) {
            columns.add(new FailureModesTableField("nextEffect-" + i, "Next Effect"));
        }

        List<List<Map<String, Object>>> rowLists = table.getRows().stream().map(r -> {
            FaultEvent treeRoot = faultEventRepositoryService.findRequired(r.getFinalEffect());

            Map<String, Object> row = new HashMap<>();

            row.put("id", r.getUri().toString());
            row.put("rowId", r.getUri().toString());

            // TODO be less strict due to deleted events??
            FaultEvent localEffect = faultEventRepositoryService.findRequired(r.getLocalEffect());
            row.put("localEffect", localEffect.getName());

            List<FaultEvent> treePathList = FaultTreeTraversalUtils
                    .rootToLeafPath(treeRoot, localEffect.getUri());

            Collections.reverse(treePathList);

            List<Pair<FaultEvent, Integer>> indexedNextEffects = IntStream.range(0, treePathList.size())
                    .boxed()
                    .map(index -> Pair.of(treePathList.get(index), index))
                    .filter(pair -> !pair.getFirst().getUri().equals(treeRoot.getUri()))
                    .filter(pair -> r.getEffects().contains(pair.getFirst().getUri()))
                    .collect(Collectors.toList());

            for (Pair<FaultEvent, Integer> pair : indexedNextEffects) {
                row.put("nextEffect-" + (maxEffects - pair.getSecond() - 1), pair.getFirst().getName());
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
