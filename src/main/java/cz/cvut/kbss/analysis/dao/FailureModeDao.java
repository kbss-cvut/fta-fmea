package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.model.FailureMode;
import cz.cvut.kbss.jopa.model.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class FailureModeDao {

    private final EntityManager em;

    @Autowired
    public FailureModeDao(EntityManager em) {
        this.em = em;
    }

    public List<FailureMode> findAll() {
        return em
                .createNamedQuery("FailureMode.findAll", FailureMode.class)
                .getResultList();
    }

    public void persist(FailureMode failureMode) {
        assert failureMode != null;
        assert failureMode.getUri() != null;

        em.persist(failureMode);

        log.debug("FailureMode {} persisted.", failureMode);
    }

    public void delete(FailureMode failureMode) {
        assert failureMode != null;

        final FailureMode toRemove = em.merge(failureMode);
        em.remove(toRemove);

        log.debug("FailureMode {} deleted.", failureMode);
    }
}
