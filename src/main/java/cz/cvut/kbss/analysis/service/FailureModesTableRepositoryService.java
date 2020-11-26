package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FailureModesTableDao;
import cz.cvut.kbss.analysis.dto.update.FailureModesTableUpdateDTO;
import cz.cvut.kbss.analysis.exception.EntityNotFoundException;
import cz.cvut.kbss.analysis.model.FailureModesTable;
import cz.cvut.kbss.analysis.model.FaultTree;
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
public class FailureModesTableRepositoryService {

    private final FailureModesTableDao failureModesTableDao;

    @Transactional(readOnly = true)
    public List<FailureModesTable> findAll() {
        return failureModesTableDao.findAll();
    }

    @Transactional
    public FailureModesTable update(FailureModesTableUpdateDTO updateDTO) {
        log.info("> update - {}", updateDTO);

        FailureModesTable table = getFailureModesTable(updateDTO.getUri());
        updateDTO.copyToEntity(table);
        failureModesTableDao.update(table);

        log.info("< update - {}", table);
        return table;
    }

    @Transactional
    public void delete(URI tableUri) {
        failureModesTableDao.remove(tableUri);
    }

    private FailureModesTable getFailureModesTable(URI tableIri) {
        return failureModesTableDao
                .find(tableIri)
                .orElseThrow(() -> new EntityNotFoundException("Failed to find failure modes table"));
    }

}
