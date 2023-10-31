package cz.cvut.kbss.analysis.dao;

import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.config.conf.RepositoryConf;
import cz.cvut.kbss.analysis.environment.TransactionalTestRunner;
import cz.cvut.kbss.analysis.environment.config.TestPersistenceConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class, classes = {
        TestPersistenceConfig.class,
        PersistenceConf.class,
        RepositoryConf.class,
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public abstract class BaseDaoTestRunner extends TransactionalTestRunner {
}
