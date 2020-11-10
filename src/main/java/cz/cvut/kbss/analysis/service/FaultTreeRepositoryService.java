package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FaultTreeDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.model.FaultTree;
import cz.cvut.kbss.analysis.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FaultTreeRepositoryService {

    private final FaultTreeDao faultTreeDao;

    @Transactional(readOnly = true)
    public List<FaultTree> findAllForUser(User user) {
        return faultTreeDao.findAllForUser(user);
    }

    @Transactional(readOnly = true)
    public FaultTree find(URI faultTreeUri) {
        return getFaultTree(faultTreeUri);
    }

    @Transactional
    public FaultTree create(FaultTree faultTree){
        faultTreeDao.persist(faultTree);
        return faultTree;
    }

    @Transactional
    public void update(FaultTree faultTree) {
        faultTreeDao.update(faultTree);
    }

    private FaultTree getFaultTree(URI faultTreeUri) {
        return faultTreeDao
                .find(faultTreeUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find fault tree"));
    }

}
