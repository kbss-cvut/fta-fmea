package cz.cvut.kbss.analysis.service.validation;

import cz.cvut.kbss.analysis.dao.ComponentDao;
import cz.cvut.kbss.analysis.exception.ValidationException;
import cz.cvut.kbss.analysis.model.Component;
import cz.cvut.kbss.analysis.util.Vocabulary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@org.springframework.stereotype.Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ComponentValidator {

    private final ComponentDao componentDao;

    public void validateDuplicates(Component component) {
        log.info("> validateDuplicates");

        boolean duplicate = componentDao.existsWithPredicate(Vocabulary.s_p_hasName, component.getName());
        if(duplicate) {
            String message = "Component has duplicate name!!";
            log.warn("< validateDuplicates - {}", message);
            throw new ValidationException(message);
        }

        log.info("< validateDuplicates - component unique");
    }

}
