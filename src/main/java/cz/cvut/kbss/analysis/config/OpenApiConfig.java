package cz.cvut.kbss.analysis.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ForwardedHeaderFilter;

/**
 * Configuration class for OpenAPI documentation.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "FTA and FMEA API",
                description = "Docs for FTA and FMEA API",
                version = "1.0"
        )
)
@SecuritySchemes({
        @SecurityScheme(
                name = "basicAuth",
                description = "Basic auth",
                scheme = "basic",
                type = SecuritySchemeType.HTTP,
                in = SecuritySchemeIn.HEADER
        )
})
@Configuration
public class OpenApiConfig {
        /**
         * Overwrites OpenAPI server url, i.e. url where REST API is accessible.
         * This is useful when running the application behind a reverse proxy.
         *
         * Whole url should be provided, including protocol and port, e.g.
         * <code>https://example.com/production/performance-server</code>
         */
        @Value("${api.base-url}")
        private String apiBaseUrl;

        @Bean
        ForwardedHeaderFilter forwardedHeaderFilter() {
                return new ForwardedHeaderFilter();
        }

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                        .addServersItem(new Server()
                                .url(apiBaseUrl)
                                .description("Production Server"));
        }
}
