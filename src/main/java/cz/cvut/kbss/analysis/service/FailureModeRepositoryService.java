package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.ComponentDao;
import cz.cvut.kbss.analysis.dao.FailureModeDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class FailureModeRepositoryService extends BaseRepositoryService<FailureMode> {

    private final FailureModeDao failureModeDao;

    @Override
    protected GenericDao<FailureMode> getPrimaryDao() {
        return failureModeDao;
    }

    @Transactional(readOnly = true)
    public Set<Mitigation> getMitigation(URI failureModeUri) {
        FailureMode failureMode = findRequired(failureModeUri);

        return failureMode.getMitigation();
    }

    @Transactional
    public Mitigation addMitigation(URI failureModeUri, Mitigation mitigation) {
        log.info("> addMitigation - {}, {}", failureModeUri, mitigation);

        FailureMode failureMode = findRequired(failureModeUri);

        failureMode.addMitigation(mitigation);
        update(failureMode);

        log.info("< addMitigation - {}", mitigation);
        return mitigation;
    }

}