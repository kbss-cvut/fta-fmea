package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.ComponentDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.dao.SystemDao;
import cz.cvut.kbss.analysis.dao.UserDao;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.model.Item;
import cz.cvut.kbss.analysis.model.System;
import cz.cvut.kbss.analysis.model.opdata.OperationalDataFilter;
import cz.cvut.kbss.analysis.service.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class SystemRepositoryService extends ComplexManagedEntityRepositoryService<System> {

    private final SystemDao systemDao;
    private final ComponentRepositoryService componentRepositoryService;
    private final ComponentDao componentDao;
    private final OperationalDataFilterService operationalDataFilterService;

    @Autowired
    public SystemRepositoryService(@Qualifier("defaultValidator") Validator validator,
                                   SystemDao systemDao,
                                   ComponentRepositoryService componentRepositoryService,
                                   ComponentDao componentDao,
                                   UserDao userDao,
                                   SecurityUtils securityUtils,
                                   OperationalDataFilterService operationalDataFilterService) {
        super(validator, userDao, securityUtils);
        this.systemDao = systemDao;
        this.componentRepositoryService = componentRepositoryService;
        this.componentDao = componentDao;
        this.operationalDataFilterService = operationalDataFilterService;
    }

    @Override
    protected GenericDao<System> getPrimaryDao() {
        return systemDao;
    }

    @Transactional
    public System rename(System systemRename) {
        log.info("> rename - {}", systemRename);

        System system = findRequired(systemRename.getUri());
        system.setName(systemRename.getName());
        update(system);

        log.info("< rename - {}", systemRename);
        return systemRename;
    }

    @Transactional
    public void addComponent(URI systemUri, URI componentUri) {
        log.info("> addComponent - {}, {}", systemUri, componentUri);

        System system = findRequired(systemUri);
        Component component = componentRepositoryService.findRequired(componentUri);

        system.addComponent(component);
        update(system);

        log.info("< addComponent");
    }

    @Transactional
    public void removeComponent(URI systemUri, URI componentUri) {
        log.info("> removeComponent - {}, {}", systemUri, componentUri);

        System system = findRequired(systemUri);

        system.getComponents().removeIf(c -> c.getUri().equals(componentUri));
        update(system);

        log.info("< removeComponent");
    }

    @Transactional
    public Set<FailureMode> getAllFailureModes(URI systemUri) {
        System system = findRequired(systemUri);
        Set<FailureMode> failureModes = new HashSet<>();
        for(Item comp: system.getComponents()) {
            failureModes.addAll(comp.getFailureModes());
        }
        return failureModes;
    }

    @Transactional
    public void importDocument(URI systemURI, URI contextIRI) {
        log.info("importing components from context <{}> into the system <{}>", contextIRI, systemURI);
        List<Component> components = componentDao.findAll(contextIRI);
        System system = systemDao.find(systemURI).get();
        components.forEach(system::addComponent);
    }

    @Transactional(readOnly = true)
    public List<System> findAllSummaries(){
        List<System> systems = ((SystemDao)getPrimaryDao()).findAllSummaries();
        OperationalDataFilter globalFilter = operationalDataFilterService.getDefaultGlobalFilter();
        for(System system : systems){
            OperationalDataFilter filter = operationalDataFilterService.getSystemFilter(system.getUri());
            system.setOperationalDataFilter(filter);
            system.setGlobalOperationalDataFilter(globalFilter);
        }
        return systems;
    }
}
