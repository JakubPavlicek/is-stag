package com.stag.platform.gateway.config;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

/// **JWT Configuration with Enhanced Caching and Resilience**
///
/// Configures an enhanced JWT decoder with advanced caching, refresh-ahead strategies, and outage tolerance for production environments.
/// This configuration is **disabled** for the `perftest` profile.
///
/// @see <a href="https://connect2id.com/products/nimbus-jose-jwt/examples/enhanced-jwk-retrieval">JWKs Retrieval</a>
/// @see <a href="https://github.com/spring-projects/spring-security/pull/17046">JwkSource Pull Request</a>
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Profile("!perftest")
@Slf4j
@Configuration
public class JwtConfig {

    /// JWK Set URI from application configuration (e.g., Keycloak endpoint)
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    /// Creates an enhanced JWT decoder with caching and resilience features.
    ///
    /// @return Configured `JwtDecoder` with enhanced JWK source
    /// @throws MalformedURLException if the JWK Set URI is invalid
    @Bean
    @Primary
    public JwtDecoder enhancedJwtDecoder() throws MalformedURLException {
        // Convert a configured URI string to URL object
        URL jwkSetURL = URI.create(jwkSetUri).toURL();

        // Build the Nimbus-enhanced JWK source with caching and retry capabilities
        JWKSource<SecurityContext> jwkSource = JWKSourceBuilder.create(jwkSetURL)
                                                               .cache(MINUTES.toMillis(15), SECONDS.toMillis(10))
                                                               .refreshAheadCache(MINUTES.toMillis(1), true)
                                                               .outageTolerant(HOURS.toMillis(4))
                                                               .retrying(true)
                                                               .build();

        return NimbusJwtDecoder.withJwkSource(jwkSource).build();
    }

}