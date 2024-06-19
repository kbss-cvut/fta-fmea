package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FailureModeDao;
import cz.cvut.kbss.analysis.dao.FaultTreeDao;
import cz.cvut.kbss.analysis.model.Event;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.FaultTree;
import cz.cvut.kbss.analysis.model.Item;
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

    public FaultTreeService(FaultEventRepositoryService faultEventRepositoryService, FailureModeDao failureModeDao, FaultTreeRepositoryService faultTreeRepositoryService, FaultTreeDao faultTreeDao) {
        this.faultEventRepositoryService = faultEventRepositoryService;
        this.failureModeDao = failureModeDao;
        this.faultTreeRepositoryService = faultTreeRepositoryService;
        this.faultTreeDao = faultTreeDao;
    }

    public FaultTree findWithDetails(URI id) {
        FaultTree ft = faultTreeRepositoryService.findRequired(id);
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
        ft.setSystem(summary.getSystem());
        ft.setSubsystem(summary.getSubsystem());

        return ft;
    }

    protected void setReferences(FaultTree faultTree){
        if(faultTree.getManifestingEvent() == null)
            return;

        Stack<Pair<URI, FaultEvent>> stack = new Stack<>();
        stack.add(Pair.of(null,faultTree.getManifestingEvent()));
        while(!stack.isEmpty()){
            Pair<URI,FaultEvent> p = stack.pop();
            FaultEvent fe = p.getSecond();
            faultEventRepositoryService.setExternalReference(p.getFirst(), fe);
            if(fe.getChildren() == null)
                continue;
            fe.getChildren().forEach(c -> stack.push(Pair.of(fe.getUri(), c)));
        }
    }

    protected void setRelatedBehaviors(Collection<Event> events){
        for(Event event : events){
            if(event.getBehavior() == null)
                event.setBehavior(failureModeDao.findByEvent(event.getUri()));
        }
    }
}
