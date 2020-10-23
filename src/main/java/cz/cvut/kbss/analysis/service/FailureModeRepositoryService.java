package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FailureModeDao;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FailureModeRepositoryService {

    private final FailureModeDao failureModeDao;

    @Transactional(readOnly = true)
    public FailureMode find(URI failureModeUri) {
        return getNode(failureModeUri);
    }

    @Transactional(readOnly = true)
    public Set<Mitigation> getMitigation(URI failureModeUri) {
        FailureMode failureMode = getNode(failureModeUri);

        return failureMode.getMitigation();
    }

    @Transactional
    public Mitigation addMitigation(URI failureModeUri, Mitigation mitigation) {
        FailureMode failureMode = getNode(failureModeUri);

        failureMode.addMitigation(mitigation);
        failureModeDao.update(failureMode);

        return mitigation;
    }

    @Transactional
    public TreeNode setManifestingEvent(URI failureModeUri, FaultEvent manifestingEvent) {
        FailureMode failureMode = getNode(failureModeUri);

        // insert a gate in between
        Gate intermediateGate = new Gate();
        TreeNode intermediateNode = new TreeNode(intermediateGate);

        TreeNode manifestingNode = new TreeNode(manifestingEvent);
        intermediateNode.addChild(manifestingNode);

        failureMode.setManifestingNode(intermediateNode);

        failureModeDao.update(failureMode);

        return manifestingNode;
    }

    private FailureMode getNode(URI failureModeUri) {
        return failureModeDao
                .find(failureModeUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find failure mode"));
    }

}