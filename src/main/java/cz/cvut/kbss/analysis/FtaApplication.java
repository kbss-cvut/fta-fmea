package cz.cvut.kbss.analysis;

import cz.cvut.kbss.analysis.model.util.EntityToOwlClassMapper;
import cz.cvut.kbss.jopa.model.EntityManagerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FtaApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(FtaApplication.class, args);
        // init
        EntityManagerFactory f = ctx.getBean(EntityManagerFactory.class);
        EntityToOwlClassMapper.init(f.getMetamodel().getEntities());
    }

}
