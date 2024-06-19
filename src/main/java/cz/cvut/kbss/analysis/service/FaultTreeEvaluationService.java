package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.model.FaultTree;
import cz.cvut.kbss.analysis.model.opdata.OperationalDataFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@Slf4j
public class FaultTreeEvaluationService
{
    private final FaultTreeRepositoryService faultTreeRepositoryService;
    private final OperationalDataFilterService operationalDataFilterService;
    private final FaultTreeService faultTreeService;

    public FaultTreeEvaluationService(FaultTreeRepositoryService faultTreeRepositoryService, OperationalDataFilterService operationalDataFilterService, FaultTreeService faultTreeService) {
        this.faultTreeRepositoryService = faultTreeRepositoryService;
        this.operationalDataFilterService = operationalDataFilterService;
        this.faultTreeService = faultTreeService;
    }

    public FaultTree evaluate(URI faultTreeUri, OperationalDataFilter filter) {

        operationalDataFilterService.updateFaultTreeFilter(faultTreeUri, filter);

        FaultTree faultTree = faultTreeService.findWithDetails(faultTreeUri);

        faultTreeRepositoryService.updateFaultTreeOperationalFailureRates(faultTree, filter);

        return faultTreeRepositoryService.evaluate(faultTree);
    }
}
