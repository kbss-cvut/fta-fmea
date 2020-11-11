package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FaultEventDao;
import cz.cvut.kbss.analysis.dao.GateDao;
import cz.cvut.kbss.analysis.dao.TreeNodeDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.exception.LogicViolationException;
import cz.cvut.kbss.analysis.model.*;
import cz.cvut.kbss.analysis.model.util.TreeNodeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventRepositoryService {

    private final TreeNodeDao treeNodeDao;

    @Transactional
    public void updateNode(TreeNode node) {
        treeNodeDao.update(node);
    }

    @Transactional
    public TreeNode addInputEvent(URI nodeUri, FaultEvent inputEvent) {
        TreeNode node = getNode(nodeUri);

        TreeNode currentGate;
        if (node.getNodeType() == TreeNodeType.EVENT) {
            if (!node.getChildren().isEmpty()) {
                log.info("Reusing existing gate. Event can only have one child gate.");
                currentGate = node.getChildren().iterator().next();
            } else {
                log.info("Inserting intermediate gate in below event...");
                TreeNode intermediateNode = new TreeNode(new Gate());
                node.addChild(intermediateNode);
                treeNodeDao.update(node);
                currentGate = intermediateNode;
            }
        } else {
            currentGate = node;
        }

        TreeNode inputEventNode = new TreeNode(inputEvent);
        currentGate.addChild(inputEventNode);
        treeNodeDao.update(currentGate);

        return inputEventNode;
    }

    @Transactional(readOnly = true)
    public Set<Event> getInputEvents(URI nodeUri) {
        TreeNode node = getNode(nodeUri);

        return node.getChildren().stream().map(TreeNode::getEvent).collect(Collectors.toSet());
    }

    @Transactional
    public TreeNode insertGate(URI nodeUri, Gate gate) {
        TreeNode node = getNode(nodeUri);

        if (node.getNodeType() != TreeNodeType.EVENT) {
            throw new LogicViolationException("Cannot insert gate under gate!");
        }

        if (!node.getChildren().isEmpty()) {
            throw new LogicViolationException("Event already has a gate");
        }

        TreeNode gateNode = new TreeNode(gate);
        node.addChild(gateNode);
        treeNodeDao.update(node);

        return gateNode;
    }

    private TreeNode getNode(URI nodeUri) {
        return treeNodeDao
                .find(nodeUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find tree node"));
    }

}
