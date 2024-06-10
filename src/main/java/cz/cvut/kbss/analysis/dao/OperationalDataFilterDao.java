package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.model.opdata.OperationalDataFilter;
import cz.cvut.kbss.analysis.service.IdentifierService;
import cz.cvut.kbss.analysis.util.Vocabulary;
import cz.cvut.kbss.jopa.model.EntityManager;
import cz.cvut.kbss.jopa.model.descriptors.EntityDescriptor;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.util.Objects;

@Repository
public class OperationalDataFilterDao extends BaseDao<OperationalDataFilter> {

    protected static final URI HAS_OPERATIONAL_DATA_FILTER_PROP = URI.create(Vocabulary.s_p_has_operational_data_filter);

    public OperationalDataFilterDao( EntityManager em, PersistenceConf config, IdentifierService identifierService) {
        super(OperationalDataFilter.class, em, config, identifierService);
    }

    public OperationalDataFilter findByEntity(URI entity) {
        return em.createNativeQuery("""
                SELECT ?uri WHERE {?entity ?hasOperationalDataFilter ?uri } LIMIT 1
                """, OperationalDataFilter.class)
                .setParameter("hasOperationalDataFilter", HAS_OPERATIONAL_DATA_FILTER_PROP)
                .setParameter("entity", entity)
                .getResultList().stream().findAny().orElse(null);
    }

    /**
     * Associates the filter with the entityURI
     * @param entityURI
     * @param filter should have non-null uri and context
     */
    public void persistHasFilter(URI entityURI, OperationalDataFilter filter){

        Objects.requireNonNull(entityURI);
        Objects.requireNonNull(filter);
        Objects.requireNonNull(filter.getUri());

        em.createNativeQuery("""
                INSERT {
                    GRAPH ?context{
                        ?entity ?hasOperationalDataFilter ?filter.
                    }
                }WHERE {}
                """)
                .setParameter("context", filter.getContext())
                .setParameter("entity", entityURI)
                .setParameter("hasOperationalDataFilter", HAS_OPERATIONAL_DATA_FILTER_PROP)
                .setParameter("filter", filter.getUri())
                .executeUpdate();
    }

    @Override
    public EntityDescriptor getEntityDescriptor(URI uri) {
        return super.getEntityDescriptor(uri);
    }
}
