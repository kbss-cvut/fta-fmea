package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.dao.SystemDao;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.System;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.net.URI;

@Service
@Slf4j
public class SystemRepositoryService extends BaseRepositoryService<System> {

    private final SystemDao systemDao;
    private final ComponentRepositoryService componentRepositoryService;

    @Autowired
    public SystemRepositoryService(@Qualifier("defaultValidator") Validator validator, SystemDao systemDao, ComponentRepositoryService componentRepositoryService) {
        super(validator);
        this.systemDao = systemDao;
        this.componentRepositoryService = componentRepositoryService;
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

}
