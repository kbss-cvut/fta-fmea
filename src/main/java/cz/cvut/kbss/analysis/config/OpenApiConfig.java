package cz.cvut.kbss.analysis.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ForwardedHeaderFilter;

/**
 * Configuration class for OpenAPI documentation.
 */
@Configuration
@Slf4j
public class OpenApiConfig {
    /**
     * Application version
     */
    @Value("${application.version:development}")
    private String version;


    /**
     * Overwrites OpenAPI server url, i.e. url where REST API is accessible.
     * This is useful when running the application behind a reverse proxy.
     * <p>
     * Whole url should be provided, including protocol and port, e.g.
     * <code>https://example.com/production/performance-server</code>
     */
    @Value("${api.base-url}")
    private String apiBaseUrl;

    @Value("${security.provider}")
    private String securityProvider;

    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        log.info("Creating OpenAPI - <{}>, {}", apiBaseUrl, securityProvider);

        return switch (securityProvider == null ? "" : securityProvider) {
            case "internal" -> apiKeyOpenAPI();
            case "oidc" -> httpOpenAPI();
            default -> commonOpenAPI();
        };
    }

    protected OpenAPI commonOpenAPI() {
        return new OpenAPI().addServersItem(new Server()
                        .url(apiBaseUrl)
                        .description("Production Server"))
                .info(new io.swagger.v3.oas.models.info.Info().title("FTA and FMEA API")
                        .description("Docs for FTA and FMEA API.")
                        .version(version));
    }

    public OpenAPI customOpenAPI(String schemeKey, SecurityScheme scheme) {
        return commonOpenAPI().components(new Components()
                .addSecuritySchemes(schemeKey, scheme))
        .addSecurityItem(new SecurityRequirement().addList(schemeKey));
    }

    public OpenAPI apiKeyOpenAPI() {
        String description = """
        To use the `Try it out` feature - login (for example using FTA FMEA UI or cURL) and make sure that the returned JSESSIONID cookie is set in the browser window where you run swagger.
        #### Login Using cURL
        Linux:
        ```
        curl --location '%sauth/signin' \\
        --form 'username="..."' \\
        --form 'password="..."'
        ```
        
        Windows:
        ```
        curl -i ^
        --form username="..." ^
        --form password="..." ^
        %s/auth/signin
        ```
        """.formatted(apiBaseUrl, apiBaseUrl);
        OpenAPI openAPI = commonOpenAPI();
        openAPI.getInfo().setDescription(openAPI.getInfo().getDescription() + "<br/><br/>" + description);
        return openAPI;
    }

    public OpenAPI httpOpenAPI() {
        return customOpenAPI(
                "bearer-key",
                new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")
        );
    }
}
