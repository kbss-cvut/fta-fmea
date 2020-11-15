package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.TreeNodeDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.exception.LogicViolationException;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.TreeNode;
import cz.cvut.kbss.analysis.model.util.EventType;
import cz.cvut.kbss.analysis.service.validation.FaultEventValidator;
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
    private final FaultEventValidator faultEventValidator;

    @Transactional
    public void delete(URI nodeUri) {
        treeNodeDao.remove(nodeUri);
    }

    @Transactional
    public void updateNode(TreeNode node) {
        faultEventValidator.validate(node.getEvent());

        treeNodeDao.update(node);
    }

    @Transactional
    public TreeNode addInputEvent(URI nodeUri, FaultEvent inputEvent) {
        TreeNode currentNode = getNode(nodeUri);

        faultEventValidator.validate(inputEvent);

        TreeNode inputEventNode = new TreeNode(inputEvent);
        currentNode.addChild(inputEventNode);

        treeNodeDao.update(currentNode);

        return inputEventNode;
    }

    private TreeNode getNode(URI nodeUri) {
        return treeNodeDao
                .find(nodeUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find tree node"));
    }

}
