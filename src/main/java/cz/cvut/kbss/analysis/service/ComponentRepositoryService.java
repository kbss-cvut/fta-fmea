package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.ComponentDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.dto.update.ComponentUpdateDTO;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.model.Function;
import cz.cvut.kbss.analysis.service.validation.EntityValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class ComponentRepositoryService extends BaseRepositoryService<Component> {

    private final ComponentDao componentDao;
    private final FailureModeRepositoryService failureModeRepositoryService;
    private final FunctionRepositoryService functionService;

    @Autowired
    public ComponentRepositoryService(@Qualifier("componentValidator") EntityValidator validator, ComponentDao componentDao
            , FailureModeRepositoryService failureModeRepositoryService, FunctionRepositoryService functionService) {
        super(validator);
        this.componentDao = componentDao;
        this.failureModeRepositoryService = failureModeRepositoryService;
        this.functionService = functionService;
    }

    @Override
    protected GenericDao<Component> getPrimaryDao() {
        return componentDao;
    }

    @Transactional
    public Component updateByDTO(ComponentUpdateDTO updateDTO) {
        log.info("> updateByDTO - {}", updateDTO);

        Component component = findRequired(updateDTO.getUri());
        updateDTO.copyToEntity(component);

        update(component);

        return component;
    }

    @Transactional
    public Function addFunction(URI componentUri, Function function) {
        log.info("> addFunction - {}, {}", componentUri, function);

        Component component = findRequired(componentUri);

        component.addFunction(function);
        update(component);
        function.setItem(component);

        log.info("< addFunction - {}", function);
        return function;
    }

    @Transactional
    public Function addFunctionByURI(URI componentUri, URI functionUri) {
        log.info("> addFunction - {}, {}", componentUri, functionUri);

        Component component = findRequired(componentUri);
        Function function = functionService.findRequired(functionUri);
        component.addFunction(function);
        update(component);

        log.info("< addFunction - {}", function);
        return function;
    }

    @Transactional(readOnly = true)
    public Set<Function> getFunctions(URI componentUri) {
        Component component = findRequired(componentUri);

        return component.getFunctions();
    }

    @Transactional
    public Set<FailureMode> getFailureModes(URI componentUri) {
        Component component = findRequired(componentUri);
        return component.getFailureModes();
    }

    @Transactional
    public void deleteFunction(URI componentUri, URI functionUri) {
        log.info("> deleteFunction - {}, {}", componentUri, functionUri);

        Component component = findRequired(componentUri);

        component.getFunctions()
                .stream()
                .filter(function -> function.getUri().equals(functionUri)).findFirst().ifPresent(function -> {
                    function.setItem(null);
                    component.getFunctions().remove(function);
                });

        update(component);

        log.info("> deleteFunction - deleted");
    }


    @Transactional
    public FailureMode addFailureMode(URI componentUri, FailureMode failureMode) {
        log.info("> addFailureMode - {}, {}", componentUri, failureMode);

        failureModeRepositoryService.validateNew(failureMode);

        Component component = findRequired(componentUri);
        component.addFailureMode(failureMode);
        update(component);
        log.info("< addFailureMode - {}, {}", componentUri, failureMode);
        return failureMode;
    }

    @Transactional
    public void addFailureModeByUri(URI componentUri, URI failureModeUri) {
        log.info("> addFailureModeByUri - {}, {}", componentUri, failureModeUri);
        Component component = findRequired(componentUri);
        FailureMode failureMode = failureModeRepositoryService.findRequired(failureModeUri);
        component.addFailureMode(failureMode);
        update(component);
    }

    @Transactional
    public void deleteFailureMode(URI componentUri, URI failureModeUri) {
        log.info("> deleteFailureMode - {}, {}", componentUri, failureModeUri);

        Component component = findRequired(componentUri);
        FailureMode failureMode = failureModeRepositoryService.findRequired(failureModeUri);
        component
                .getFailureModes()
                .removeIf(function -> function.getUri().equals(failureModeUri));

        update(component);
        failureMode.setItem(null);
        failureModeRepositoryService.update(failureMode);
        log.info("> deleteFailureMode - deleted");
    }


    @Transactional
    public Component linkComponents(URI componentUri, URI linkComponentUri) {
        log.info("> linkComponents - {}, {}", componentUri, linkComponentUri);

        Component component = findRequired(componentUri);
        Component linkComponent = findRequired(linkComponentUri);

        linkComponent.addComponent(component);
        update(component);
        update(linkComponent);

        log.info("< linkComponents");
        return component;
    }

    @Transactional
    public void unlinkComponents(URI componentUri) {
        log.info("> unlinkComponents - {}", componentUri);

        Component component = findRequired(componentUri);

        component.setParentComponent(null);
        update(component);

        log.info("< unlinkComponents");
    }

    @Transactional
    public void mergeComponents(URI sourceComponentUri, URI targetComponentUri){
        Component source = findRequired(sourceComponentUri);
        Component target = findRequired(targetComponentUri);

        source.getFunctions().forEach(target::addFunction);
        source.getFailureModes().forEach(target::addFailureMode);

        source.setFunctions(new HashSet<>());
        source.setFailureModes(new HashSet<>());

        findAll().stream()
                .filter(component -> component.getParentComponent() != null
                        && component.getParentComponent().getUri().equals(sourceComponentUri)
                )
                .forEach(component -> {
                    target.addComponent(component);
                    update(component);
                });

        update(target);
        remove(source);
    }
}