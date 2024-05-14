package cz.cvut.kbss.analysis.util;

import cz.cvut.kbss.analysis.config.conf.SecurityConf;
import cz.cvut.kbss.analysis.security.SecurityConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OidcGrantedAuthoritiesExtractorTest {

    @Mock
    private SecurityConf config;

    @Test
    void convertMapsTopLevelClaimWithRolesToGrantedAuthorities() {
        when(config.getRoleClaim()).thenReturn("roles");
        final List<String> roles = List.of(SecurityConstants.ROLE_ADMIN, SecurityConstants.ROLE_USER);
        final Jwt token = Jwt.withTokenValue("abcdef12345")
                             .header("alg", "RS256")
                             .header("typ", "JWT")
                             .claim("roles", roles)
                             .issuer("http://localhost:8080/termit")
                             .subject("termit")
                             .expiresAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(300))
                             .build();

        final OidcGrantedAuthoritiesExtractor sut = new OidcGrantedAuthoritiesExtractor(config);
        final Collection<SimpleGrantedAuthority> result = sut.convert(token);
        assertNotNull(result);
        for (String r : roles) {
            assertThat(result, hasItem(new SimpleGrantedAuthority(r)));
        }
    }

    @Test
    void convertSupportsNestedRolesClaim() {
        when(config.getRoleClaim()).thenReturn("realm_access.roles");
        final List<String> roles = List.of(SecurityConstants.ROLE_ADMIN, SecurityConstants.ROLE_USER);
        final Jwt token = Jwt.withTokenValue("abcdef12345")
                             .header("alg", "RS256")
                             .header("typ", "JWT")
                             .claim("realm_access", Map.of("roles", roles))
                             .issuer("http://localhost:8080/fta-fmea")
                             .subject("termit")
                             .expiresAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(300))
                             .build();

        final OidcGrantedAuthoritiesExtractor sut = new OidcGrantedAuthoritiesExtractor(config);
        final Collection<SimpleGrantedAuthority> result = sut.convert(token);
        assertNotNull(result);
        for (String r : roles) {
            assertThat(result, hasItem(new SimpleGrantedAuthority(r)));
        }
    }

    @Test
    void convertThrowsIllegalArgumentExceptionWhenExpectedClaimPathIsNotTraversable() {
        when(config.getRoleClaim()).thenReturn("realm_access.roles.list");
        final Jwt token = Jwt.withTokenValue("abcdef12345")
                             .header("alg", "RS256")
                             .header("typ", "JWT")
                             .claim("realm_access", Map.of("roles", 1235))
                             .issuer("http://localhost:8080/fta-fmea")
                             .subject("termit")
                             .expiresAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(300))
                             .build();

        final OidcGrantedAuthoritiesExtractor sut = new OidcGrantedAuthoritiesExtractor(config);
        assertThrows(IllegalArgumentException.class, () -> sut.convert(token));
    }

    @Test
    void convertThrowsIllegalArgumentExceptionWhenNestedRolesClaimIsNotList() {
        when(config.getRoleClaim()).thenReturn("realm_access.roles.notlist");
        final Jwt token = Jwt.withTokenValue("abcdef12345")
                             .header("alg", "RS256")
                             .header("typ", "JWT")
                             .claim("realm_access", Map.of("roles", Map.of("notlist", SecurityConstants.ROLE_USER)))
                             .issuer("http://localhost:8080/fta-fmea")
                             .subject("termit")
                             .expiresAt(Instant.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(300))
                             .build();

        final OidcGrantedAuthoritiesExtractor sut = new OidcGrantedAuthoritiesExtractor(config);
        assertThrows(IllegalArgumentException.class, () -> sut.convert(token));
    }
}