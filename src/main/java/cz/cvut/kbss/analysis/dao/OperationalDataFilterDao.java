package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.opdata.OperationalDataFilter;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.util.Optional;

@Repository
public class OperationalDataFilterDao extends BaseDao<OperationalDataFilter> {

    protected static final URI HAS_OPERATIONAL_DATA_FILTER_PROP = URI.create(Vocabulary.s_p_has_operational_data_filter);

    public OperationalDataFilterDao( EntityManager em, PersistenceConf config, IdentifierService identifierService) {
        super(OperationalDataFilter.class, em, config, identifierService);
    }

    public OperationalDataFilter findByEntity(URI entity) {
         Optional<URI> optUri = em.createNativeQuery("""
                SELECT ?uri WHERE {?entity ?hasOperationalDataFilter ?uri } LIMIT 1
                """, URI.class)
                .setParameter("hasOperationalDataFilter", HAS_OPERATIONAL_DATA_FILTER_PROP)
                .setParameter("entity", entity)
                .getResultList().stream().findAny();

        return optUri.map( u -> find(u).orElse(null)).orElse(null);
    }

    /**
     * Associates the filter with the entityURI
     * @param entityURI
     * @param filter should have non-null uri and context
     */
    public void persistHasFilter(URI entityURI, OperationalDataFilter filter){
        addOrReplaceValue(entityURI, HAS_OPERATIONAL_DATA_FILTER_PROP, filter, filter.getContext());
    }

    @Override
    public EntityDescriptor getEntityDescriptor(URI uri) {
        EntityDescriptor entityDescriptor = super.getEntityDescriptor(uri);
        URI context = getContext(uri);
        entityDescriptor.addContext(context);
        return entityDescriptor;
    }
}
