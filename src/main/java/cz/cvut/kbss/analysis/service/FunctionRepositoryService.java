package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.dao.FailureModeDao;
import cz.cvut.kbss.analysis.dao.FunctionDao;
import cz.cvut.kbss.analysis.dao.GenericDao;
import cz.cvut.kbss.analysis.model.Behavior;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.model.Function;
import cz.cvut.kbss.analysis.service.validation.EntityValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class FunctionRepositoryService extends BaseRepositoryService<Function> {

    private final FunctionDao functionDao;
    private final FailureModeDao failureModeDao;

    @Autowired
    public FunctionRepositoryService(@Qualifier("functionValidator") EntityValidator validator, FunctionDao functionDao, FailureModeDao failureModeDao) {
        super(validator);
        this.functionDao = functionDao;
        this.failureModeDao = failureModeDao;
    }

    @Override
    protected GenericDao<Function> getPrimaryDao() {
        return functionDao;
    }

    @Transactional
    public Function updateFunction(Function funcToUpdate){
        Function function = findRequired(funcToUpdate.getUri());
        function.setName(funcToUpdate.getName());
        function.setRequiredBehaviors(loadFunctions(funcToUpdate.getRequiredBehaviors()));
        function.setImpairedBehaviors(loadFailureModes(funcToUpdate.getImpairedBehaviors()));
        function.setChildBehaviors(loadFunctions(funcToUpdate.getChildBehaviors()));
        function.setBehaviorType(funcToUpdate.getBehaviorType());
        return update(function);
    }

    protected Set<Behavior> loadFunctions(Collection<Behavior> behaviors){
        return load(behaviors, b -> functionDao.find(b.getUri()).get());
    }

    protected Set<Behavior> loadFailureModes(Collection<Behavior> behaviors){
        return load(behaviors, b -> failureModeDao.find(b.getUri()).get());
    }

    protected Set<Behavior> load(Collection<Behavior> behaviors, java.util.function.Function<Behavior, Behavior> loader){
        Set<Behavior> ret = new HashSet<>();
        for(Behavior b : behaviors){
            Behavior loaded = loader.apply(b);
            if(loaded != null){
                ret.add(loaded);
            }
        }
        return ret;
    }

    @Transactional(readOnly = true)
    public Set<Behavior> getRequiredBehavior(URI functionUri) {
        Function function = findRequired(functionUri);
        return function.getRequiredBehaviors();
    }

    @Transactional
    public Function addRequiredBehavior(URI functionUri, URI requiredBehaviorUri) {
        log.info("> addRequiredFunction - {}, {}", functionUri, requiredBehaviorUri);

        Function function = findRequired(functionUri);
        Behavior behavior = findRequired(requiredBehaviorUri);
        function.addRequiredBehavior(behavior);

        update(function);
        log.info("< addRequiredFunction - {}", function);
        return function;
    }


    @Transactional
    public void deleteRequiredBehavior(URI functionUri, URI requiredFunction) {
        log.info("> deleteFunction - {}, {}", functionUri, requiredFunction);

        Function function = findRequired(functionUri);
        function.getRequiredBehaviors()
                .removeIf(f -> f.getUri().equals(requiredFunction));

        update(function);

        log.info("> deleteFunction - deleted");
    }

    @Transactional
    public Component getComponent(URI functionUri){
        log.info("> getComponent - {}", functionUri);
        return functionDao.getComponent(functionUri);
    }

    @Transactional
    public List<Behavior> getImpairingBehaviors(URI functionUri){
        log.info("> getImpairedBehaviors - {}", functionUri);
        return functionDao.getImpairingBehaviors(functionUri);
    }

    @Transactional
    public void addChildBehavior(URI functionUri, URI childFunctionUri) {
        log.info("> addChildBehavior - {}, {}", functionUri, childFunctionUri);

        Function function = findRequired(functionUri);
        function.getChildBehaviors().add(findRequired(childFunctionUri));

        update(function);
    }

    @Transactional
    public void removeChildBehavior(URI functionUri, URI childFunctionUri) {
        log.info("> removeChildBehavior - {}, {}", functionUri, childFunctionUri);

        Function function = findRequired(functionUri);
        function.getChildBehaviors().removeIf(behavior -> behavior.getUri().equals(childFunctionUri));

        update(function);
        log.info("> removeChildBehavior - removed");
    }

    @Transactional
    public Set<URI> getTransitiveClosure(URI functionUri, String type) {
        log.info("> get{}TransitiveClosure - {}", type, functionUri);
        Set<URI> transitiveFunctions;

        if (type.equals("child")) {
            transitiveFunctions = functionDao.getIndirectBehaviorParts(functionUri);
        }else if(type.equals("required")) {
            transitiveFunctions = functionDao.getIndirectRequiredBehaviors(functionUri);
        }else{
            transitiveFunctions = functionDao.getIndirectImpairingBehaviors(functionUri);
        }
        return transitiveFunctions;
    }
}
