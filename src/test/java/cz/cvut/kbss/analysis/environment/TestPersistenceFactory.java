package cz.cvut.kbss.analysis.environment;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.config.conf.RepositoryConf;
import cz.cvut.kbss.jopa.Persistence;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProvider;
import cz.cvut.kbss.ontodriver.rdf4j.config.Rdf4jOntoDriverProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

import static cz.cvut.kbss.jopa.model.JOPAPersistenceProperties.*;

@TestConfiguration
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class, classes = {PersistenceConf.class, RepositoryConf.class})
@ActiveProfiles("test")
public class TestPersistenceFactory {

    private final RepositoryConf repositoryConf;

    private final PersistenceConf persistenceConf;

    private EntityManagerFactory emf;

    @Autowired
    public TestPersistenceFactory(RepositoryConf repositoryConf, PersistenceConf persistenceConf) {
        this.repositoryConf = repositoryConf;
        this.persistenceConf = persistenceConf;
    }

    @Bean
    @Primary
    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    /**
     * Default persistence unit configuration parameters.
     *
     * <p>These include: package scan for entities, provider specification
     *
     * @return Map with defaults
     */
    public static Map<String, String> defaultParams() {
        final Map<String, String> map = new HashMap<>();
        map.put(SCAN_PACKAGE, "cz.cvut.kbss.analysis.model");
        map.put(JPA_PERSISTENCE_PROVIDER, JOPAPersistenceProvider.class.getName());
        return map;
    }

    @PostConstruct
    private void init() {
        final Map<String, String> properties = defaultParams();
        properties.put(ONTOLOGY_PHYSICAL_URI_KEY, repositoryConf.getUrl());
        properties.put(Rdf4jOntoDriverProperties.USE_VOLATILE_STORAGE, Boolean.TRUE.toString());
        properties.put(DATA_SOURCE_CLASS, persistenceConf.getDriver());
        properties.put(LANG, persistenceConf.getLanguage());
        properties.put(CACHE_ENABLED, "false");

        // OPTIMIZATION: Always use statement retrieval with unbound property. Should spare
        // repository queries
        properties.put(Rdf4jOntoDriverProperties.LOAD_ALL_THRESHOLD, "1");
        this.emf = Persistence.createEntityManagerFactory("fta-fmea-test", properties);
    }

    @PreDestroy
    private void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
