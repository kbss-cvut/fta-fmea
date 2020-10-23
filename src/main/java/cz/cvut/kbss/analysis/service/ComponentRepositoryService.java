package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.ComponentDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.Function;
import cz.cvut.kbss.analysis.model.User;
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

    @Transactional(readOnly = true)
    public List<Component> findAllForUser(User user) {
        return componentDao.findAllForUser(user);
    }

    @Transactional(readOnly = true)
    public Component persist(Component component) {
        componentDao.persist(component);
        return component;
    }

    @Transactional
    public URI addFunction(URI componentUri, Function function) {
        Component component = componentDao
                .find(componentUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find component"));

        component.addFunction(function);
        componentDao.update(component);

        return function.getUri();
    }

    @Transactional(readOnly = true)
    public Set<Function> getFunctions(URI componentUri) {
        Component component = componentDao
                .find(componentUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find component"));

        return component.getFunctions();
    }

}