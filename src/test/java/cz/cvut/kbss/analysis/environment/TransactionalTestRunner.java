package cz.cvut.kbss.analysis.environment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;

public abstract class TransactionalTestRunner {

    @Autowired
    protected PlatformTransactionManager txManager;

    protected void transactional(Runnable procedure) {
        Transaction.execute(txManager, procedure);
    }
}
