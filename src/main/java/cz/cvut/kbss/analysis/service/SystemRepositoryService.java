package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.ComponentDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.dao.SystemDao;
import cz.cvut.kbss.analysis.dao.UserDao;
import cz.cvut.kbss.analysis.exception.LogicViolationException;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.model.Item;
import cz.cvut.kbss.analysis.model.System;
import cz.cvut.kbss.analysis.model.opdata.OperationalDataFilter;
import cz.cvut.kbss.analysis.service.security.SecurityUtils;
import cz.cvut.kbss.analysis.service.validation.EntityValidator;
import cz.cvut.kbss.analysis.util.Vocabulary;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class SystemRepositoryService extends ComplexManagedEntityRepositoryService<System> {

    public static final URI GENERAL_SYSTEM_CONTEXT = URI.create(Vocabulary.s_c_ata_system + "/tool-context");

    private final SystemDao systemDao;
    private final ComponentRepositoryService componentRepositoryService;
    private final ComponentDao componentDao;
    private final OperationalDataFilterService operationalDataFilterService;

    @Autowired
    public SystemRepositoryService(@Qualifier("systemValidator") EntityValidator validator,
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

    // TODO check if this works for lazy loading
    @Transactional
    @Override
    public System findRequired(URI id) {
        System system = super.findRequired(id);
        // fetch partonomy
        systemDao.findComponents(id);

        return system;
    }

    @Transactional
    public System create(System system){
        this.persist(system);
        return system;
    }

    @Override
    public void remove(@NonNull URI instanceUri) {
        System system = findAllSummary(instanceUri);
        List<URI> faultTrees = systemDao.findSystemsFaultTrees(instanceUri);
        if(!faultTrees.isEmpty()) {

            throw new LogicViolationException((
                    "Cannot delete system \"%s\" (<%s>), " +
                            "the system has fault %d trees.")
                    .formatted(system.getName(), instanceUri, faultTrees.size()));
        }
        super.remove(instanceUri);
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
        for(System system : systems)
            setOperationalDataFilter(system);
        return systems;
    }

    @Transactional(readOnly = true)
    public System findAllSummary(URI systemUri){
        System system = ((SystemDao)getPrimaryDao()).findSummary(systemUri);
        setOperationalDataFilter(system);
        return system;
    }

    protected void setOperationalDataFilter(System system) {
        OperationalDataFilter filter = operationalDataFilterService.getSystemFilter(system.getUri());
        system.setOperationalDataFilter(filter);
        system.setGlobalOperationalDataFilter(operationalDataFilterService.getDefaultGlobalFilter());
    }

    public URI getToolContextForGeneralSystem(URI uri){
        return GENERAL_SYSTEM_CONTEXT;
    }

}
