package cz.cvut.kbss.analysis.service;


import cz.cvut.kbss.analysis.dao.ManagedEntityDao;
import cz.cvut.kbss.analysis.dao.UserDao;
import cz.cvut.kbss.analysis.model.ManagedEntity;
import cz.cvut.kbss.analysis.model.UserReference;
import cz.cvut.kbss.analysis.service.security.SecurityUtils;
import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * A common repository service for entities stored in their own context, e.g. System, FaultTree, along with their parts,
 * e.g. for System its components and their behaviors, for FaultTrees its FaultEvents.
 * @param <T>
 */
public abstract class ComplexManagedEntityRepositoryService<T extends ManagedEntity> extends BaseRepositoryService<T> {
    protected final UserDao userDao;

    public ComplexManagedEntityRepositoryService(Validator validator, UserDao userDao) {
        super(validator);
        this.userDao = userDao;
    }

    @Override
    protected void preUpdate(@NonNull T instance) {
        super.preUpdate(instance);
        UserReference user = SecurityUtils.currentUserReference();
        instance.setLastEditor(user);
        instance.setModified(new Date());
    }

    @Override
    protected void prePersist(@NonNull T instance) {
        super.prePersist(instance);
        UserReference user = SecurityUtils.currentUserReference();
        instance.setCreator(user);
        instance.setCreated(new Date());
    }


    @Transactional(readOnly = true)
    public List<T> findAllSummaries(){
        return ((ManagedEntityDao<T>)getPrimaryDao()).findAllSummaries().stream().map(this::postLoad).toList();
    }


    @Override
    protected T postLoad(@NonNull T instance) {
        super.postLoad(instance);
        for(UserReference user :Arrays.asList(instance.getLastEditor(), instance.getCreator())){
            if(user == null) continue;
            setUserName(user);
        }
        return instance;
    }

    protected void setUserName(UserReference user){
        String username = userDao.findUsername(user.getUri());
        user.setUsername(username);
    }

}
