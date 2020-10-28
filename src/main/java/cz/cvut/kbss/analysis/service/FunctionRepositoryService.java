package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FailureModeDao;
import cz.cvut.kbss.analysis.dao.FunctionDao;
import cz.cvut.kbss.analysis.dto.URIReference;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.analysis.model.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FunctionRepositoryService {

    private final FunctionDao functionDao;
    private final FailureModeDao failureModeDao;

    @Transactional
    public FailureMode addFailureMode(URI functionUri, URIReference failureModeReference) {
        Function function = functionDao
                .find(functionUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find component"));

        FailureMode failureMode = failureModeDao
                .find(failureModeReference.getUri())
                .orElseThrow(() -> new EntityNotFoundException("Failed to find failure mode"));

        function.addFailureMode(failureMode);
        functionDao.update(function);

        return failureMode;
    }

    @Transactional(readOnly = true)
    public Set<FailureMode> getFailureModes(URI functionUri) {
        Function function = functionDao
                .find(functionUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find component"));

        return function.getFailureModes();
    }
}
