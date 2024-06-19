package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.FailureRateEstimate;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import org.springframework.stereotype.Repository;

import java.net.URI;

@Repository
public class FailureRateEstimateDao extends BaseDao<FailureRateEstimate>{
    protected final static URI HAS_ESTIMATE_PROP = URI.create(Vocabulary.s_p_has_estimate);
    protected final static URI VALUE_PROP = URI.create(Vocabulary.s_p_value);

    public FailureRateEstimateDao(EntityManager em, PersistenceConf config, IdentifierService identifierService) {
        super(FailureRateEstimate.class, em, config, identifierService);
    }

    public void setHasEstimate(URI failureRateUri, FailureRateEstimate estimate, URI context) {
        addOrReplaceValue(failureRateUri, HAS_ESTIMATE_PROP, estimate.getUri(), context);
    }

    public void setValue(URI failureRateEstimateUri, Double value, URI context) {
        addOrReplaceValue(failureRateEstimateUri, VALUE_PROP, value, context);
    }
}
