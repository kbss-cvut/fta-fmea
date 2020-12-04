package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.dao.SystemDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.exception.LogicViolationException;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.System;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class SystemRepositoryService extends BaseRepositoryService<System> {

    private final SystemDao systemDao;
    private final ComponentRepositoryService componentRepositoryService;

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
