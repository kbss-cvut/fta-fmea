package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FailureModesRowDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.dto.update.FailureModesRowRpnUpdateDTO;
import cz.cvut.kbss.analysis.model.FailureModesRow;
import cz.cvut.kbss.analysis.model.Mitigation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.net.URI;

@Slf4j
@Service
public class FailureModesRowRepositoryService extends BaseRepositoryService<FailureModesRow> {

    private final FailureModesRowDao failureModesRowDao;
    private final MitigationRepositoryService mitigationRepositoryService;

    @Autowired
    public FailureModesRowRepositoryService(@Qualifier("defaultValidator") Validator validator, FailureModesRowDao failureModesRowDao
            ,MitigationRepositoryService mitigationRepositoryService) {
        super(validator);
        this.failureModesRowDao = failureModesRowDao;
        this.mitigationRepositoryService = mitigationRepositoryService;
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


        if(rowRpnUpdateDTO.getMitigationUri().equals("")){
            failureModesRow.setMitigation(null);
        }else {
            Mitigation mitigation = mitigationRepositoryService.findRequired(URI.create(rowRpnUpdateDTO.getMitigationUri()));
            failureModesRow.setMitigation(mitigation);
        }

        update(failureModesRow);

        log.info("< updateByDTO");
    }
}
