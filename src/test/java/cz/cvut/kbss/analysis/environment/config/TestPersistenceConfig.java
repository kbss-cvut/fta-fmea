package cz.cvut.kbss.analysis.environment.config;

import com.github.ledsoft.jopa.spring.transaction.DelegatingEntityManager;
import com.github.ledsoft.jopa.spring.transaction.JopaTransactionManager;
import cz.cvut.kbss.analysis.environment.TestPersistenceFactory;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@TestConfiguration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = "cz.cvut.kbss.analysis.persistence")
@Import({TestPersistenceFactory.class})
@EnableTransactionManagement
public class TestPersistenceConfig {

    @Bean
    public DelegatingEntityManager entityManager() {
        return new DelegatingEntityManager();
    }

    @Bean(name = "txManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf,
                                                         DelegatingEntityManager emProxy) {
        return new JopaTransactionManager(emf, emProxy);
    }
}
