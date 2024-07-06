package cz.cvut.kbss.analysis.config;

import cz.cvut.kbss.analysis.config.conf.SecurityConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@ConditionalOnProperty(name = "security.provider", havingValue = "none",  matchIfMissing = true)
@Configuration
public class NoSecurityConfig {

    private static final Logger LOG = LoggerFactory.getLogger(NoSecurityConfig.class);

    private final SecurityConf config;

    @Autowired
    public NoSecurityConfig(SecurityConf config) {
        LOG.info("Initializing NoSecurityConfig");
        this.config = config;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .cors(auth -> auth.configurationSource(SecurityConfig.createCorsConfiguration(config)))
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
