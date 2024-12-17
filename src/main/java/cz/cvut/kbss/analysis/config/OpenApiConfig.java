package cz.cvut.kbss.analysis.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;

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
public class OpenApiConfig {
}
