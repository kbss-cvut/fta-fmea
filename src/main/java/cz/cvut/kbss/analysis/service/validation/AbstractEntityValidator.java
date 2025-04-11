package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.dao.BaseDao;
import cz.cvut.kbss.analysis.model.AbstractEntity;
import cz.cvut.kbss.analysis.service.validation.groups.ValidationScopes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public abstract class AbstractEntityValidator<T extends AbstractEntity> implements EntityValidator {

    protected final Class<T> supporetedClass;
    protected final SpringValidatorAdapter validatorAdapter;


    @Override
    public boolean supports(Class<?> clazz) {
        return supporetedClass.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors, Object... validationHints) {
        validatorAdapter.validate(target, errors, validationHints);
        customValidation((T)target, errors, validationHints);
    }

    @Override
    public void validate(Object target, Errors errors) {
        validatorAdapter.validate(target, errors);
        customValidation((T)target, errors);
    }

    protected abstract BaseDao<T> getPrimaryDao();

    protected void customValidation(T target, Errors errors, Object... validationHints ){
        ConstraintGroupsAdapter groups = new ConstraintGroupsAdapter(validationHints);
        customValidation(target, errors, groups, validationHints);
    }

    protected void customValidation(T target, Errors errors, ConstraintGroupsAdapter groups, Object... validationHints ){
        if(groups.isCreateGroup() && exists(target)){
            errors.rejectValue("uri", "uri.exists", "The uri should be null or unique");
        }
        if(groups.isUpdateGroup() && !exists(target)){
            errors.rejectValue("uri", "uri.not-exists", "Uri does not refer to an existing entity");
        }
    }



    protected boolean exists(T entity){
        return entity.getUri() != null && getPrimaryDao().exists(entity.getUri());
    }


    public static class ConstraintGroupsAdapter{
        Set<Class> groups;

        public ConstraintGroupsAdapter(Object... groups) {
            this.groups = classSet(groups);
        }

        public Set<Class> classSet(Object... groups){
            return Stream.of(groups)
                    .filter(o -> o != null)
                    .filter(o -> o instanceof Class)
                    .map(o -> (Class<?>)o)
                    .collect(Collectors.toSet());
        }

        public boolean matchesAll(Object ... groups) {
            if(groups.length == 0) return true;
            return classSet(groups).stream().allMatch(t -> this.groups.stream().anyMatch(g -> g.isAssignableFrom(t)));
        }

        public boolean isCreateGroup() {
            return matchesAll(ValidationScopes.Create.class);
        }
        public boolean isUpdateGroup() {
            return matchesAll(ValidationScopes.Update.class);
        }
    }
}
