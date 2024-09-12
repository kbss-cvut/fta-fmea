package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.dao.MitigationDao;
import cz.cvut.kbss.analysis.dto.update.MitigationUpdateDTO;
import cz.cvut.kbss.analysis.model.Mitigation;
import cz.cvut.kbss.analysis.service.validation.EntityValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@Slf4j
@Service
public class MitigationRepositoryService extends BaseRepositoryService<Mitigation> {

    private final MitigationDao mitigationDao;

    @Autowired
    public MitigationRepositoryService(@Qualifier("defaultEntityValidator") EntityValidator validator, MitigationDao mitigationDao) {
        super(validator);
        this.mitigationDao = mitigationDao;
    }

    @Transactional
    public Mitigation update(MitigationUpdateDTO updateDTO){
        log.info("> updateByDTO - {}", updateDTO);
        Mitigation mitigation = new Mitigation();
        if(updateDTO.getUri() == null){
            updateDTO.copyToEntity(mitigation);
            persist(mitigation);
            return mitigation;
        }

        mitigation = findRequired(updateDTO.getUri());
        updateDTO.copyToEntity(mitigation);
        update(mitigation);

        return mitigation;
    }

    @Override
    protected GenericDao<Mitigation> getPrimaryDao() {
        return mitigationDao;
    }

    @Transactional
    public void remove(URI mitigationUri){
        mitigationDao.remove(mitigationUri);
    }

    @Transactional
    public Mitigation create(Mitigation mitigation) {
        mitigationDao.persist(mitigation);
        return mitigation;
    }
}
