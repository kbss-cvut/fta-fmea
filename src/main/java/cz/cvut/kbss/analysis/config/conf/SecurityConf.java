package cz.cvut.kbss.analysis.config.conf;

import cz.cvut.kbss.analysis.util.ConfigParam;
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

    private String rolePrefix;

    private String roleClaim;

    @Autowired
    public SecurityConf(Environment env) {
        allowedOrigins = env.getProperty(ConfigParam.CORS_ALLOWED_ORIGINS.toString());
        appContext = env.getProperty(ConfigParam.APP_CONTEXT.toString());
        rolePrefix = env.getProperty(ConfigParam.ROLE_PREFIX.toString());
        roleClaim = env.getProperty(ConfigParam.OIDC_ROLE_CLAIM.toString());
    }
}
