package com.stag.platform.gateway.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class JwtRoleConverterTest {

    private final JwtRoleConverter converter = new JwtRoleConverter();

    @Test
    @DisplayName("should convert valid Keycloak roles to GrantedAuthorities")
    void convert_ValidRoles_ReturnsAuthorities() {
        Map<String, Object> realmAccess = Map.of("roles", List.of("ST", "AD"));
        Map<String, Object> claims = Map.of("realm_access", realmAccess);
        
        Jwt jwt = Jwt.withTokenValue("token")
                     .header("alg", "none")
                     .claims(c -> c.putAll(claims))
                     .issuedAt(Instant.now())
                     .expiresAt(Instant.now().plusSeconds(3600))
                     .build();

        StepVerifier.create(Objects.requireNonNull(converter.convert(jwt)))
                    .assertNext(auth -> assertThat(auth.getAuthority()).isEqualTo("ROLE_ST"))
                    .assertNext(auth -> assertThat(auth.getAuthority()).isEqualTo("ROLE_AD"))
                    .verifyComplete();
    }

    @Test
    @DisplayName("should return empty flux when realm_access claim is missing")
    void convert_MissingRealmAccess_ReturnsEmpty() {
        Jwt jwt = Jwt.withTokenValue("token")
                     .header("alg", "none")
                     .claim("sub", "user")
                     .issuedAt(Instant.now())
                     .expiresAt(Instant.now().plusSeconds(3600))
                     .build();

        StepVerifier.create(Objects.requireNonNull(converter.convert(jwt)))
                    .verifyComplete();
    }

    @Test
    @DisplayName("should return empty flux when roles list is empty")
    void convert_EmptyRoles_ReturnsEmpty() {
        Map<String, Object> realmAccess = Map.of("roles", Collections.emptyList());
        Map<String, Object> claims = Map.of("realm_access", realmAccess);
        
        Jwt jwt = Jwt.withTokenValue("token")
                     .header("alg", "none")
                     .claims(c -> c.putAll(claims))
                     .issuedAt(Instant.now())
                     .expiresAt(Instant.now().plusSeconds(3600))
                     .build();

        StepVerifier.create(Objects.requireNonNull(converter.convert(jwt)))
                    .verifyComplete();
    }

    @Test
    @DisplayName("should return empty flux when realm_access is null")
    void convert_NullRealmAccess_ReturnsEmpty() {
        Jwt jwt = Jwt.withTokenValue("token")
                     .header("alg", "none")
                     .claim("realm_access", null)
                     .issuedAt(Instant.now())
                     .expiresAt(Instant.now().plusSeconds(3600))
                     .build();

        StepVerifier.create(Objects.requireNonNull(converter.convert(jwt)))
                    .verifyComplete();
    }
}
