package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FunctionDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import java.net.URI;
import java.util.Set;

@Slf4j
@Service
public class FunctionRepositoryService extends BaseRepositoryService<Function> {

    private final FunctionDao functionDao;

    @Autowired
    public FunctionRepositoryService(@Qualifier("functionValidator") Validator validator, FunctionDao functionDao) {
        super(validator);
        this.functionDao = functionDao;
    }

    @Override
    protected GenericDao<Function> getPrimaryDao() {
        return functionDao;
    }

    @Transactional(readOnly = true)
    public Set<Function> getFunctions(URI functionUri) {
        Function function = findRequired(functionUri);
        return function.getRequiredFunctions();
    }

    @Transactional
    public Function addRequiredFunction(URI functionUri, URI requiredFunctionUri) {
        log.info("> addRequiredFunction - {}, {}", functionUri, requiredFunctionUri);

        Function function = findRequired(functionUri);
        Function requiredFunction = findRequired(requiredFunctionUri);
        function.addFunction(requiredFunction);

        update(function);
        log.info("< addRequiredFunction - {}", function);
        return function;
    }


    @Transactional
    public void deleteFunction(URI functionUri, URI dependentFunctionUri) {
        log.info("> deleteFunction - {}, {}", functionUri, dependentFunctionUri);

        Function function = findRequired(functionUri);
        function.getRequiredFunctions()
                .removeIf(f -> f.getUri().equals(dependentFunctionUri));

        update(function);

        log.info("> deleteFunction - deleted");
    }

    @Transactional
    public Component getComponent(URI functionUri){
        log.info("> getComponent - {}", functionUri);
        return functionDao.getComponent(functionUri);
    }
}
