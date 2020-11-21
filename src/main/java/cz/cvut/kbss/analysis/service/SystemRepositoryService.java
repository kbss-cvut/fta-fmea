package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.SystemDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
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
public class SystemRepositoryService {

    private final SystemDao systemDao;

    @Transactional(readOnly = true)
    public List<System> findAll() {
        return systemDao.findAll();
    }

    @Transactional(readOnly = true)
    public System find(URI systemUri) {
        log.info("> find - {}", systemUri);

        return getSystem(systemUri);
    }

    @Transactional
    public System create(System system) {
        log.info("> create - {}", system);

        systemDao.persist(system);

        log.info("< create - {}", system);
        return system;
    }

    @Transactional
    public System update(System system) {
        log.info("> update - {}", system);

        systemDao.update(system);

        log.info("< update - {}", system);
        return system;
    }

    @Transactional
    public void delete(URI systemUri) {
        systemDao.remove(systemUri);
    }

    private System getSystem(URI systemUri) {
        return systemDao
                .find(systemUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find system"));
    }

}
