package cz.cvut.kbss.analysis.service;

import cz.cvut.kbss.analysis.config.conf.JwtConf;
import cz.cvut.kbss.analysis.config.conf.PersistenceConf;
import cz.cvut.kbss.analysis.config.conf.RepositoryConf;
import cz.cvut.kbss.analysis.dao.*;
import cz.cvut.kbss.analysis.environment.TransactionalTestRunner;
import cz.cvut.kbss.analysis.environment.config.TestPersistenceConfig;
import cz.cvut.kbss.analysis.environment.config.TestServiceConfig;
import cz.cvut.kbss.analysis.service.validation.FaultEventValidator;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@ExtendWith(SpringExtension.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableTransactionManagement
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class,
        classes = {
                TestServiceConfig.class,
                TestPersistenceConfig.class,

                PersistenceConf.class,
                RepositoryConf.class,
                JwtConf.class,

                UserDao.class,
                FaultEventDao.class,
                FaultTreeDao.class,
                ComponentDao.class,
                FailureModeDao.class,
                FailureModesRowDao.class,
                FailureModesTableDao.class,
                SystemDao.class,

                FaultEventValidator.class,
                ComponentRepositoryService.class,
        })
@ActiveProfiles("test")
public class BaseServiceTestRunner extends TransactionalTestRunner {
}
