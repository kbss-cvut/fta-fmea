package cz.cvut.kbss.analysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.kbss.analysis.dao.ComponentDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.dao.SystemDao;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.model.System;
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
public class SystemRepositoryService extends BaseRepositoryService<System> {

    private final SystemDao systemDao;
    private final ComponentRepositoryService componentRepositoryService;
    private final ComponentDao componentDao;

    @Autowired
    public SystemRepositoryService(@Qualifier("defaultValidator") Validator validator, SystemDao systemDao, ComponentRepositoryService componentRepositoryService, ComponentDao componentDao) {
        super(validator);
        this.systemDao = systemDao;
        this.componentRepositoryService = componentRepositoryService;
        this.componentDao = componentDao;
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
        for(Component comp: system.getComponents()) {
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
        return ((SystemDao)getPrimaryDao()).findAllSummaries();
    }
}
