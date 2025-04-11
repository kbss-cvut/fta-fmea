package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.dao.BaseDao;
import cz.cvut.kbss.analysis.dao.UserDao;
import cz.cvut.kbss.analysis.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@Qualifier("userValidator")
@Slf4j
@Component
public class UserValidator extends AbstractEntityValidator<User>{

    protected final UserDao userDao;

    public UserValidator(SpringValidatorAdapter validatorAdapter, UserDao userDao) {
        super(User.class, validatorAdapter);
        this.userDao = userDao;
    }

    @Override
    protected BaseDao<User> getPrimaryDao() {
        return userDao;
    }
}
