package cz.cvut.kbss.analysis.service;

import com.opencsv.CSVWriter;
import cz.cvut.kbss.analysis.dao.FailureModesTableDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.dto.table.FailureModesTableDataDTO;
import cz.cvut.kbss.analysis.dto.table.FailureModesTableField;
import cz.cvut.kbss.analysis.dto.update.FailureModesTableUpdateDTO;
import cz.cvut.kbss.analysis.model.*;
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
    private final FunctionRepositoryService functionRepositoryService;

    @Autowired
    public FailureModesTableRepositoryService(@Qualifier("defaultValidator") Validator validator,
                                              FailureModesTableDao failureModesTableDao,
                                              FaultEventRepositoryService faultEventRepositoryService,
                                              FunctionRepositoryService functionRepositoryService) {
        super(validator);
        this.failureModesTableDao = failureModesTableDao;
        this.faultEventRepositoryService = faultEventRepositoryService;
        this.functionRepositoryService = functionRepositoryService;
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
        columns.add(new FailureModesTableField("cause", "Cause"));
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

        List<List<Map<String, Object>>> rowLists = table.getRows().stream().map(r -> {
            FaultEvent treeRoot = faultEventRepositoryService.findRequired(r.getFinalEffect());

            Map<String, Object> row = new HashMap<>();
            List<String> causes = new ArrayList<>();

            row.put("id", r.getUri().toString());
            row.put("rowId", r.getUri().toString());

            FaultEvent localEffect = faultEventRepositoryService.findRequired(r.getLocalEffect());

            List<FaultEvent> treePathList = FaultTreeTraversalUtils
                    .rootToLeafPath(treeRoot, localEffect.getUri());

            int treeIndex = getFEIndexWithFunction(treePathList);
            FaultEvent functionFailure = treePathList.get(treeIndex);

            Function function = functionFailure.getFunction();
            row.put("function", function.getName());

            Component component = function.getComponent();
            if(component != null) row.put("component", component.getName());

            // TODO be less strict due to deleted events??
            FailureMode failureMode = localEffect.getFailureMode();

            functionRepositoryService.getImpairingBehaviors(function.getUri())
                    .stream()
                    .filter(Behavior::isFailureModeCause)
                    .forEach(cause -> causes.add(cause.getName()));

            if (failureMode != null) {
                failureMode.getRequiredBehaviors()
                        .stream()
                        .filter(Behavior::isFailureModeCause)
                        .forEach(cause -> causes.add(cause.getName()));

                row.put("failureMode", failureMode.getName());
                failureMode.getImpairedBehaviors()
                        .stream()
                        .filter(Mitigation.class::isInstance)
                        .forEach(mitigation -> row.put("mitigation", mitigation.getDescription()));
            }

            String localEffectUri = "" ;
            if(treeIndex + 1  < treePathList.size()){
                localEffect = treePathList.get(treeIndex + 1);

                if (localEffect.getBehavior() != null
                        && !treeRoot.getName().equals(localEffect.getName())
                        && localEffect.getBehavior().getComponent() == function.getComponent()) {
                    row.put("localEffect", localEffect.getName());
                }
                localEffectUri = localEffect.getUri().toString();

            }

            Collections.reverse(treePathList);
            String localEffectUriCopy = localEffectUri;
            List<Pair<FaultEvent, Integer>> indexedNextEffects = IntStream.range(0, treePathList.size())
                    .boxed()
                    .map(index -> Pair.of(treePathList.get(index), index))
                    .filter(pair -> !pair.getFirst().getUri().equals(treeRoot.getUri()))
                    .filter(pair -> r.getEffects().contains(pair.getFirst().getUri()))
                    .filter(pair -> !pair.getFirst().getUri().equals(functionFailure.getUri()))
                    .filter(pair -> !pair.getFirst().getUri().toString().equals(localEffectUriCopy))
                    .collect(Collectors.toList());

            for (Pair<FaultEvent, Integer> pair : indexedNextEffects) {
                if (columns.stream().map(FailureModesTableField::getField)
                        .noneMatch(s -> s.equals("intermediateEffect-" + pair.getSecond()))) {
                    columns.add(new FailureModesTableField("intermediateEffect-" + pair.getSecond(), "Intermediate effect"));
                }
                row.put("intermediateEffect-" + pair.getSecond(), pair.getFirst().getName());
            }

            row.put("finalEffect", treeRoot.getName());

            RiskPriorityNumber rpn = r.getRiskPriorityNumber();
            row.put("severity", rpn.getSeverity());
            row.put("occurrence", rpn.getOccurrence());
            row.put("detection", rpn.getDetection());
            if (rpn.getSeverity() != null && rpn.getOccurrence() != null && rpn.getDetection() != null) {
                row.put("rpn", rpn.getSeverity() * rpn.getOccurrence() * rpn.getDetection());
            }

            List<Map<String, Object>> resultList = new ArrayList<>();

            if (causes.isEmpty()) {
                resultList.add(row);
            }else {
                causes.forEach(cause -> {
                    row.put("cause", cause);
                    resultList.add(row);
                });
            }

            return resultList;
        }).collect(Collectors.toList());

        return rowLists.stream().flatMap(List::stream)
                .distinct().collect(Collectors.toList());
    }

    private int getFEIndexWithFunction(List<FaultEvent> faultEventList) {
        for (int i = 0; i < faultEventList.size(); i++) {
            if (faultEventList.get(i).getFunction() != null) {
                return i;
            }
        }
        return -1;
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
