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
    private final FaultEventRepositoryService faultEventRepositoryService;

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

        faultEventValidator.validate(faultTree.getManifestingEvent());

        URI faultEventUri = faultTree.getManifestingEvent().getUri();
        if (faultEventUri != null) {
            log.info("Reusing fault event - {}", faultEventUri);
            FaultEvent faultEvent = faultEventDao
                    .find(faultEventUri)
                    .orElseThrow(() -> new LogicViolationException("Fault Event is reused but does not exists in database!"));
            faultTree.setManifestingEvent(faultEvent);
        }
        faultTreeDao.persist(faultTree);

        log.info("< create - {}", faultTree);
        return faultTree;
    }

    @Transactional
    public FaultTree update(FaultTree faultTree) {
        log.info("> update - {}", faultTree);

        propagateProbabilities(faultTree);

        faultTreeDao.update(faultTree);

        log.info("< update - {}", faultTree);
        return faultTree;
    }

    @Transactional
    public void delete(URI treeUri) {
        faultTreeDao.remove(treeUri);
    }

    private FaultTree getFaultTree(URI faultTreeUri) {
        return faultTreeDao
                .find(faultTreeUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find fault tree"));
    }

    private void propagateProbabilities(FaultTree faultTree) {
        log.info("> propagateProbabilities - {}", faultTree);

        Double recomputedProbability = faultEventRepositoryService.propagateProbability(faultTree.getManifestingEvent());

        log.info("< propagateProbabilities - {}", recomputedProbability);
    }

}
