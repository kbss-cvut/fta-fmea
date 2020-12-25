package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.ComponentDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.dto.update.ComponentUpdateDTO;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.Function;
import cz.cvut.kbss.analysis.service.validation.ComponentValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.net.URI;
import java.util.Set;

@Slf4j
@Service
public class ComponentRepositoryService extends BaseRepositoryService<Component> {

    private final ComponentDao componentDao;

    @Autowired
    public ComponentRepositoryService(@Qualifier("componentValidator") Validator validator, ComponentDao componentDao) {
        super(validator);
        this.componentDao = componentDao;
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

        log.info("< addFunction - {}", function);
        return function;
    }

    @Transactional(readOnly = true)
    public Set<Function> getFunctions(URI componentUri) {
        Component component = findRequired(componentUri);

        return component.getFunctions();
    }

    @Transactional
    public void deleteFunction(URI componentUri, URI functionUri) {
        log.info("> deleteFunction - {}, {}", componentUri, functionUri);

        Component component = findRequired(componentUri);
        component
                .getFunctions()
                .removeIf(function -> function.getUri().equals(functionUri));

        update(component);

        log.info("> deleteFunction - deleted");
    }

    @Transactional
    public Component linkComponents(URI componentUri, URI linkComponentUri) {
        log.info("> linkComponents - {}, {}", componentUri, linkComponentUri);

        Component component = findRequired(componentUri);
        Component linkComponent = findRequired(linkComponentUri);

        component.setParentComponent(linkComponent.getUri());
        update(component);

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

}