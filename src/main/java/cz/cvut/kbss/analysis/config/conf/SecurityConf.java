package cz.cvut.kbss.analysis.config.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Setter
@Getter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("security")
public class SecurityConf {
    private String allowedOrigins;

    private String appContext;

    private String roleClaim;

    @Autowired
    public SecurityConf(Environment env) {
        allowedOrigins = env.getProperty("cors.allowedOrigins");
        appContext = env.getProperty("appContext");
        roleClaim = env.getProperty("oidc.RoleClaim");
    }
}
