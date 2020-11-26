package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.FaultTree;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FaultTreeDao extends BaseDao<FaultTree> {

    @Autowired
    protected FaultTreeDao(EntityManager em, PersistenceConf config) {
        super(FaultTree.class, em, config);
    }

}
