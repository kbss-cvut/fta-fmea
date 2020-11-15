package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FaultEventDao;
import cz.cvut.kbss.analysis.dao.FaultTreeDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.exception.LogicViolationException;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.FaultTree;
import cz.cvut.kbss.analysis.model.User;
import cz.cvut.kbss.analysis.service.validation.FaultEventValidator;
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
public class FaultTreeRepositoryService {

    private final FaultTreeDao faultTreeDao;
    private final FaultEventDao faultEventDao;
    private final FaultEventValidator faultEventValidator;

    @Transactional(readOnly = true)
    public List<FaultTree> findAllForUser(User user) {
        return faultTreeDao.findAllForUser(user);
    }

    @Transactional(readOnly = true)
    public FaultTree find(URI faultTreeUri) {
        return getFaultTree(faultTreeUri);
    }

    @Transactional
    public FaultTree create(FaultTree faultTree) {
        log.info("> create - {}", faultTree);

        faultEventValidator.validate(faultTree.getManifestingNode().getEvent());

        URI faultEventUri = faultTree.getManifestingNode().getEvent().getUri();
        if (faultEventUri != null) {
            log.info("Using prefilled fault event - {}", faultEventUri);
            FaultEvent faultEvent = faultEventDao
                    .find(faultEventUri)
                    .orElseThrow(() -> new LogicViolationException("Fault Event is prefilled but does not exists in database!"));
            faultTree.getManifestingNode().setEvent(faultEvent);
        }
        faultTreeDao.persist(faultTree);

        log.info("< create - {}", faultTree);
        return faultTree;
    }

    @Transactional
    public void update(FaultTree faultTree) {
        log.info("> update - {}", faultTree);

        faultTreeDao.update(faultTree);

        log.info("< update - {}", faultTree);
    }

    private FaultTree getFaultTree(URI faultTreeUri) {
        return faultTreeDao
                .find(faultTreeUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find fault tree"));
    }

}
