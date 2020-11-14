package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.TreeNodeDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.exception.LogicViolationException;
import cz.cvut.kbss.analysis.model.FaultEvent;
import cz.cvut.kbss.analysis.model.TreeNode;
import cz.cvut.kbss.analysis.model.util.EventType;
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

    @Transactional
    public void updateNode(TreeNode node) {
        FaultEvent faultEvent = node.getEvent();

        if (faultEvent.getEventType() == EventType.INTERMEDIATE && faultEvent.getGateType() == null) {
            throw new LogicViolationException("Intermediate event must have a gate type");
        }

        treeNodeDao.update(node);
    }

    @Transactional
    public TreeNode addInputEvent(URI nodeUri, FaultEvent inputEvent) {
        TreeNode currentNode = getNode(nodeUri);

        if (currentNode.getEvent().getEventType() != EventType.INTERMEDIATE) {
            throw new LogicViolationException("Only intermediate events can have children");
        }

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
