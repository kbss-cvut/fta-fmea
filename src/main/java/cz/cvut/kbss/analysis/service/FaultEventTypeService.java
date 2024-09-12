package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FaultEventTypeDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.dao.SystemDao;
import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.model.System;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class FaultEventTypeService extends BaseRepositoryService<FaultEventType> {

    private final FaultEventTypeDao eventTypeDao;
    private final SystemDao systemDao;

    public FaultEventTypeService(FaultEventTypeDao eventTypeDao, SystemDao systemDao) {
        super(null);
        this.eventTypeDao = eventTypeDao;
        this.systemDao = systemDao;
    }

    @Transactional
    public void loadManagedSupertypesOrCreate(FaultEvent faultEvent, NamedEntity system, URI context){
        if(faultEvent.getSupertypes() == null || faultEvent.getSupertypes().isEmpty())
            return;
        Set<Event> newSupertypes = new HashSet<>();
        Set<Event> managedSupertypes = new HashSet<>();
        Set<Event> unmanagedSupertypes = faultEvent.getSupertypes();
        faultEvent.setSupertypes(managedSupertypes);

        for(Event event : unmanagedSupertypes){
            Optional<FaultEventType> opt = event.getUri() != null ?
                    eventTypeDao.find(event.getUri()) :
                    Optional.ofNullable(null);
            if(opt.isPresent())
                managedSupertypes.add(opt.get());
            else
                newSupertypes.add(event);
        }

        if(newSupertypes.isEmpty())
            return;

        System managedSystem = systemDao.find(system.getUri()).orElse(null);

        for(Event evt : newSupertypes){
            FailureMode fm = new FailureMode();
            fm.setName(evt.getName() + " failure mode");
            fm.setItem(managedSystem);
            evt.setBehavior(fm);
            evt.setContext(context);
            eventTypeDao.persist((FaultEventType) evt);
            managedSupertypes.add(evt);
        }
    }

    /**
     * Replaces the supertypes of the faultEvent argument, if any, with their managed versions
     * @param faultEvent
     */
    public void loadManagedSupertypes(FaultEvent faultEvent){
        if(faultEvent.getSupertypes() != null) {
            Set<Event> managedSupertypes = new HashSet<>();
            for(Event event : faultEvent.getSupertypes()){
                eventTypeDao.find(event.getUri()).ifPresent(managedSupertypes::add);
            }
            faultEvent.setSupertypes(managedSupertypes);
        }
    }



    @Override
    protected GenericDao<FaultEventType> getPrimaryDao() {
        return eventTypeDao;
    }

    @Override
    public void validate(FaultEventType instance, Object ... groups) {
    }
}
