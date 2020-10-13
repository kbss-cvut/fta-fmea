package cz.cvut.kbss.analysis.persistence.util;

import cz.cvut.kbss.analysis.model.HasAuthorData;
import cz.cvut.kbss.analysis.service.security.SecurityUtils;
import cz.cvut.kbss.jopa.model.annotations.PrePersist;

import java.util.Date;

/**
 * Entity listener that automatically inserts author data
 */
public class HasAuthorDataManager {

    @PrePersist
    void insertAuthorData(HasAuthorData instance) {
        instance.setAuthor(SecurityUtils.currentUser());
        instance.setCreationDate(new Date());
    }

}
