package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.Function;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FunctionDao extends BaseDao<Function> {

    @Autowired
    protected FunctionDao(EntityManager em, PersistenceConf config) {
        super(Function.class, em, config);
    }

}
