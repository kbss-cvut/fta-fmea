package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FailureModeDao;
import cz.cvut.kbss.analysis.dao.FaultTreeDao;
import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.model.opdata.OperationalDataFilter;
import cz.cvut.kbss.analysis.service.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.Stack;

@Service
@Slf4j
public class FaultTreeService{

    private final FaultEventRepositoryService faultEventRepositoryService;
    private final FailureModeDao failureModeDao;
    private final FaultTreeRepositoryService faultTreeRepositoryService;
    private final FaultTreeDao faultTreeDao;
    private final OperationalDataFilterService operationalDataFilterService;
    private final SystemRepositoryService systemRepositoryService;

    public FaultTreeService(FaultEventRepositoryService faultEventRepositoryService, FailureModeDao failureModeDao, FaultTreeRepositoryService faultTreeRepositoryService, FaultTreeDao faultTreeDao, OperationalDataFilterService operationalDataFilterService, SystemRepositoryService systemRepositoryService) {
        this.faultEventRepositoryService = faultEventRepositoryService;
        this.failureModeDao = failureModeDao;
        this.faultTreeRepositoryService = faultTreeRepositoryService;
        this.faultTreeDao = faultTreeDao;
        this.operationalDataFilterService = operationalDataFilterService;
        this.systemRepositoryService = systemRepositoryService;
    }

    public FaultTree findWithDetails(URI id) {
        FaultTree ft = faultTreeRepositoryService.findWithRelatedEventTypes(id);
        Collection<Event> events = faultTreeDao.getRelatedEventTypes(ft);
        setRelatedBehaviors(events);

        events.stream().map(e -> e.getBehavior()).forEach(b -> {
            Item item = b.getItem();
            if(item == null)
                return;

            item.setComponents(null);
            Optional.ofNullable(item.getSupertypes()).ifPresent(s -> s.forEach(st -> st.setComponents(null)));
        });

        setReferences(ft);

        FaultTree summary = faultTreeRepositoryService.findSummary(ft.getUri());
        ft.setOperationalDataFilter(summary.getOperationalDataFilter());
        ft.setSystem(Optional.ofNullable(summary).map(s -> s.getSystem()).map(s -> s.getUri())
                .map(u -> systemRepositoryService.findAllSummary(u)).orElse( null)
        );
        ft.setSubsystem(summary.getSubsystem());

        return ft;
    }

    protected void setReferences(FaultTree faultTree){
        if(faultTree.getManifestingEvent() == null)
            return;

        for(FaultEvent fe: faultTree.getAllEvents()){
            faultEventRepositoryService.setExternalReference(fe);
        }
    }

    protected void setRelatedBehaviors(Collection<Event> events){
        for(Event event : events){
            if(event.getBehavior() == null)
                event.setBehavior(failureModeDao.findByEvent(event.getUri()));
        }
    }

    public void updateFilter(URI faultTreeURI, OperationalDataFilter newFilter){
        FaultTree summary = faultTreeRepositoryService.findSummary(faultTreeURI);
        operationalDataFilterService.updateFaultTreeFilter(faultTreeURI, newFilter);
        summary.setOperationalDataFilter(newFilter);
        Status status = faultTreeRepositoryService.getInferedStatus(summary);
        faultTreeDao.updateStatus(summary.getUri(), status);
    }
}
