package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.ComponentDao;
import cz.cvut.kbss.analysis.dto.update.ComponentUpdateDTO;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.model.Function;
import cz.cvut.kbss.analysis.service.validation.ComponentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ComponentRepositoryService {

    private final ComponentDao componentDao;
    private final ComponentValidator componentValidator;

    @Transactional(readOnly = true)
    public List<Component> findAll() {
        return componentDao.findAll();
    }

    @Transactional(readOnly = true)
    public Component persist(Component component) {
        log.info("> persist - {}", component);

        componentValidator.validateDuplicates(component);
        componentDao.persist(component);
        return component;
    }

    @Transactional
    public Component update(ComponentUpdateDTO update) {
        log.info("> update - {}", update);

        Component component = getComponent(update.getUri());
        update.copyToEntity(component);
        componentDao.update(component);

        log.info("< update - {}", update);
        return component;
    }

    @Transactional
    public Function addFunction(URI componentUri, Function function) {
        log.info("> addFunction - {}, {}", componentUri, function);

        Component component = getComponent(componentUri);

        component.addFunction(function);
        componentDao.update(component);

        log.info("< addFunction - {}", function);
        return function;
    }

    @Transactional(readOnly = true)
    public Set<Function> getFunctions(URI componentUri) {
        Component component = getComponent(componentUri);

        return component.getFunctions();
    }

    @Transactional
    public FailureMode addFailureMode(URI componentUri, FailureMode failureMode) {
        log.info("> addFailureMode - {}, {}", componentUri, failureMode);

        // TODO failure mode name uniqueness

        Component component = getComponent(componentUri);

        component.addFailureMode(failureMode);
        componentDao.update(component);

        log.info("< addFailureMode - {}", failureMode);
        return failureMode;
    }

    @Transactional
    public void deleteFunction(URI componentUri, URI functionUri) {
        log.info("> deleteFunction - {}, {}", componentUri, functionUri);

        Component component = getComponent(componentUri);
        component
                .getFunctions()
                .removeIf(function -> function.getUri().equals(functionUri));

        componentDao.update(component);

        log.info("> deleteFunction - deleted");
    }

    @Transactional
    public Component linkComponents(URI componentUri, URI linkComponentUri) {
        log.info("> linkComponents - {}, {}", componentUri, linkComponentUri);

        Component component = getComponent(componentUri);
        Component linkComponent = getComponent(linkComponentUri);

        component.setParentComponent(linkComponent);
        componentDao.update(component);

        log.info("< linkComponents");
        return component;
    }

    @Transactional
    public void unlinkComponents(URI componentUri) {
        log.info("> unlinkComponents - {}", componentUri);

        Component component = getComponent(componentUri);

        component.setParentComponent(null);
        componentDao.update(component);

        log.info("< unlinkComponents");
    }

    @Transactional
    public void delete(URI componentUri) {
        componentDao.remove(componentUri);
    }

    public Component getComponent(URI componentUri) {
        return componentDao
                .find(componentUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find component"));
    }
}