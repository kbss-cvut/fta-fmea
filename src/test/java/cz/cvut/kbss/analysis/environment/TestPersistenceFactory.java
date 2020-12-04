package cz.cvut.kbss.analysis.environment;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.config.conf.RepositoryConf;
import cz.cvut.kbss.analysis.environment.config.TestServiceConfig;
import cz.cvut.kbss.analysis.persistence.MainPersistenceFactory;
import cz.cvut.kbss.jopa.Persistence;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import cz.cvut.kbss.ontodriver.sesame.config.SesameOntoDriverProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;

import static cz.cvut.kbss.jopa.model.JOPAPersistenceProperties.*;

@TestConfiguration
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class,
        classes = {TestServiceConfig.class, PersistenceConf.class, RepositoryConf.class})
@ActiveProfiles("test")
public class TestPersistenceFactory {

    private final RepositoryConf confRepository;

    private final PersistenceConf confPersistence;

    private EntityManagerFactory emf;

    @Autowired
    public TestPersistenceFactory(RepositoryConf confRepository, PersistenceConf confPersistence) {
        this.confRepository = confRepository;
        this.confPersistence = confPersistence;
    }

    @Bean
    @Primary
    public EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    @PostConstruct
    private void init() {
        final Map<String, String> properties = MainPersistenceFactory.defaultParams();
        properties.put(ONTOLOGY_PHYSICAL_URI_KEY, confRepository.getUrl());
        properties
                .put(SesameOntoDriverProperties.SESAME_USE_VOLATILE_STORAGE, Boolean.TRUE.toString());
        properties.put(SesameOntoDriverProperties.SESAME_USE_INFERENCE, Boolean.TRUE.toString());
        properties.put(DATA_SOURCE_CLASS, confPersistence.getDriver());
        properties.put(LANG, confPersistence.getLanguage());
        properties.put(CACHE_ENABLED, "false");
        // OPTIMIZATION: Always use statement retrieval with unbound property. Should spare
        // repository queries
        properties.put(SesameOntoDriverProperties.SESAME_LOAD_ALL_THRESHOLD, "1");
        properties.put(SesameOntoDriverProperties.SESAME_REPOSITORY_CONFIG, "rdf4j-memory-spin-rdfs.ttl");
        this.emf = Persistence.createEntityManagerFactory("fta-fmea-test", properties);
    }

    @PreDestroy
    private void close() {
        if (emf.isOpen()) {
            emf.close();
        }
    }
}
