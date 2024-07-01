package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.FailureRate;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.stereotype.Repository;

import java.net.URI;

@Repository
public class FailureRateDao extends BaseDao<FailureRate> {
    public static final URI HAS_FAILURE_RATE_PROP = URI.create(Vocabulary.s_p_has_failure_rate);

    public FailureRateDao(EntityManager em, PersistenceConf config, IdentifierService identifierService) {
        super(FailureRate.class, em, config, identifierService);
    }

    public void setFailureRate(URI eventUri, FailureRate fr, URI context){
        addOrReplaceValue(eventUri, HAS_FAILURE_RATE_PROP, fr.getUri(), context);

    }
}
