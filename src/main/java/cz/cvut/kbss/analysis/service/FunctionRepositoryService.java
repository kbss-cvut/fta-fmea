package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FunctionDao;
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

    @Transactional
    public FailureMode addFailureMode(URI functionUri, FailureMode failureMode) {
        Function function = functionDao
                .find(functionUri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find component"));

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
