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
@ConfigurationProperties("repository")
public class RepositoryConf {

    /**
     * URI of the repository location
     */
    private String uri;

    /**
     * Language used to store strings in the repository (persistence unit language).
     */
    private String language = "en";
}

