package com.stag.identity.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Instant;
import java.util.Map;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        return token -> Jwt.withTokenValue(token)
                           .header("alg", "none")
                           .claim("sub", "test-user")
                           .claim("realm_access", Map.of("roles", new String[]{ "AD" }))
                           .issuedAt(Instant.now())
                           .expiresAt(Instant.now().plusSeconds(3600))
                           .build();
    }

}
