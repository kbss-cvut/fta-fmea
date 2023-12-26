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
@ConfigurationProperties("security")
public class SecurityConf {
        private String allowedOrigins;
        private String appContext;
        private String issuerUri;

        private String secretKey;

        private Long expiryMs;

        private String roleClaim;
}
