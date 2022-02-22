package cz.cvut.kbss.analysis.config.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("persistence")
public class PersistenceConf {

    /**
     * OntoDriver class for the repository.
     */
    private String driver;

    /**
     * Language used to store strings in the repository (persistence unit language).
     */
    private String language = "en";
}

