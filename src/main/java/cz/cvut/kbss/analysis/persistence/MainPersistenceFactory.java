package cz.cvut.kbss.analysis.persistence;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.config.conf.RepositoryConf;
import cz.cvut.kbss.jopa.Persistence;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProperties;
import cz.cvut.kbss.jopa.model.JOPAPersistenceProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

import static cz.cvut.kbss.jopa.model.JOPAPersistenceProperties.*;
import static cz.cvut.kbss.jopa.model.PersistenceProperties.JPA_PERSISTENCE_PROVIDER;
import static cz.cvut.kbss.ontodriver.config.OntoDriverProperties.*;
import static cz.cvut.kbss.ontodriver.rdf4j.config.Rdf4jOntoDriverProperties.*;

/**
 * Sets up persistence and provides {@link EntityManagerFactory} as Spring bean.
 */
@Configuration
public class MainPersistenceFactory {

    private final RepositoryConf repositoryConf;
    private final PersistenceConf persistenceConf;

    private EntityManagerFactory emf;

    @Autowired
    public MainPersistenceFactory(RepositoryConf repositoryConf,
                                  PersistenceConf persistenceConf) {
        this.repositoryConf = repositoryConf;
        this.persistenceConf = persistenceConf;
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

    @Bean
    @Primary
    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    @PostConstruct
    private void init() {
        final Map<String, String> properties = defaultParams();

        properties.put(ONTOLOGY_PHYSICAL_URI_KEY, repositoryConf.getUrl());
        properties.put(DATA_SOURCE_CLASS, persistenceConf.getDriver());
        properties.put(LANG, persistenceConf.getLanguage());
        properties.put(CACHE_ENABLED, Boolean.FALSE.toString());

        if(repositoryConf.getUsername() != null && repositoryConf.getPassword() != null) {
            properties.put(DATA_SOURCE_USERNAME, repositoryConf.getUsername());
            properties.put(DATA_SOURCE_PASSWORD, repositoryConf.getPassword());
        }
        // OPTIMIZATION: Always use statement retrieval with unbound property. Should spare
        // repository queries
        properties.put(LOAD_ALL_THRESHOLD, "1");
        this.emf = Persistence.createEntityManagerFactory("fta-fmea", properties);
    }

    @PreDestroy
    private void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
