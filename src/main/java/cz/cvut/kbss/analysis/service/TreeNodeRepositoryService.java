package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.TreeNodeDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TreeNodeRepositoryService {

    private final TreeNodeDao treeNodeDao;

    @Transactional
    public void delete(URI nodeUri) {
        treeNodeDao.remove(nodeUri);
    }

}
