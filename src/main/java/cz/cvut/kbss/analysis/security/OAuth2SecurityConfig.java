package cz.cvut.kbss.analysis.security;


import cz.cvut.kbss.analysis.config.SecurityConfig;
import cz.cvut.kbss.analysis.config.conf.SecurityConf;
import cz.cvut.kbss.analysis.service.ConfigReader;
import cz.cvut.kbss.analysis.util.OidcGrantedAuthoritiesExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@ConditionalOnProperty(prefix = "security", name = "provider", havingValue = "oidc")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class OAuth2SecurityConfig {

    private final AuthenticationSuccess authenticationSuccess;

    private final SecurityConf config;

    @Autowired
    public OAuth2SecurityConfig(AuthenticationSuccess authenticationSuccess, SecurityConf config) {
        this.authenticationSuccess = authenticationSuccess;
        this.config = config;
    }

    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.debug("Using OAuth2/OIDC security.");
        http.oauth2ResourceServer(
                        auth -> auth.jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor())))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .exceptionHandling(ehc -> ehc.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .cors(auth -> auth.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .logout(auth -> auth.logoutUrl(SecurityConstants.LOGOUT_URI)
                        .logoutSuccessHandler(authenticationSuccess));
        return http.build();
    }

    CorsConfigurationSource corsConfigurationSource() {
        return SecurityConfig.createCorsConfiguration(config);
    }

    private Converter<Jwt, AbstractAuthenticationToken> grantedAuthoritiesExtractor() {
        return source -> {
            final Collection<SimpleGrantedAuthority> extractedRoles =
                    new OidcGrantedAuthoritiesExtractor(config).convert(source);
            assert extractedRoles != null;
            final Set<SimpleGrantedAuthority> authorities = new HashSet<>(extractedRoles);
            // Add default role if it is not present
            authorities.add(new SimpleGrantedAuthority(SecurityConstants.ROLE_USER));
            return new JwtAuthenticationToken(source, authorities);
        };
    }
}
