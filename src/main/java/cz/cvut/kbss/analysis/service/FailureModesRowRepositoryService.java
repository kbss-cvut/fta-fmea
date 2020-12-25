package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FailureModesRowDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.dto.update.FailureModesRowRpnUpdateDTO;
import cz.cvut.kbss.analysis.model.FailureModesRow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;


@Slf4j
@Service
public class FailureModesRowRepositoryService extends BaseRepositoryService<FailureModesRow> {

    private final FailureModesRowDao failureModesRowDao;

    @Autowired
    public FailureModesRowRepositoryService(@Qualifier("defaultValidator") Validator validator, FailureModesRowDao failureModesRowDao) {
        super(validator);
        this.failureModesRowDao = failureModesRowDao;
    }

    @Override
    protected GenericDao<FailureModesRow> getPrimaryDao() {
        return failureModesRowDao;
    }

    @Transactional
    public void updateByDTO(FailureModesRowRpnUpdateDTO rowRpnUpdateDTO) {
        log.info("> updateByDTO - {}", rowRpnUpdateDTO);

        FailureModesRow failureModesRow = findRequired(rowRpnUpdateDTO.getUri());
        rowRpnUpdateDTO.copyToEntity(failureModesRow);

        update(failureModesRow);

        log.info("< updateByDTO");
    }
}
