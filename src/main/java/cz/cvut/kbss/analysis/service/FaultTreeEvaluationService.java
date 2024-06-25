package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FaultTreeDao;
import cz.cvut.kbss.analysis.model.FaultTree;
import cz.cvut.kbss.analysis.model.Status;
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
    private final FaultTreeDao faultTreeDao;

    public FaultTreeEvaluationService(FaultTreeRepositoryService faultTreeRepositoryService, OperationalDataFilterService operationalDataFilterService, FaultTreeService faultTreeService, FaultTreeDao faultTreeDao) {
        this.faultTreeRepositoryService = faultTreeRepositoryService;
        this.operationalDataFilterService = operationalDataFilterService;
        this.faultTreeService = faultTreeService;
        this.faultTreeDao = faultTreeDao;
    }

    public FaultTree evaluate(URI faultTreeUri, OperationalDataFilter filter) {

        operationalDataFilterService.updateFaultTreeFilter(faultTreeUri, filter);

        FaultTree faultTree = faultTreeService.findWithDetails(faultTreeUri);

        faultTreeRepositoryService.updateFaultTreeOperationalFailureRates(faultTree, filter);

        faultTree = faultTreeRepositoryService.evaluate(faultTree);

        Status status = faultTreeRepositoryService.getInferedStatus(faultTree);
        faultTreeDao.updateStatus(faultTree.getUri(), status);

        return faultTree;


    }
}
