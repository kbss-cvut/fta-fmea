package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.System;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class SystemDao extends BaseDao<System> {

    @Autowired
    protected SystemDao(EntityManager em, PersistenceConf config) {
        super(System.class, em, config);
    }
}
