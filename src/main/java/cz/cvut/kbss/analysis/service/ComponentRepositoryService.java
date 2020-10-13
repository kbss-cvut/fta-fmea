package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.ComponentDao;
import cz.cvut.kbss.analysis.model.Component;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
public class ComponentRepositoryService {

    private final ComponentDao componentDao;

    @Autowired
    public ComponentRepositoryService(ComponentDao componentDao) {
        this.componentDao = componentDao;
    }

    @Transactional
    public List<Component> findAll() {
        return componentDao.findAll();
    }

    @Transactional
    public URI persist(Component component) {
        componentDao.persist(component);
        return component.getUri();
    }

    @Transactional
    public void delete(Component component) {
        componentDao.remove(component);
    }

}