package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.TreeNode;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TreeNodeDao extends BaseDao<TreeNode> {

    @Autowired
    public TreeNodeDao(EntityManager em, PersistenceConf config) {
        super(TreeNode.class, em, config);
    }

}
