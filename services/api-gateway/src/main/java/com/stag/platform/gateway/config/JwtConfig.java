package com.stag.platform.gateway.config;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Configuration
public class JwtConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    /// @see <a href="https://connect2id.com/products/nimbus-jose-jwt/examples/enhanced-jwk-retrieval">JWKs Retrieval</a>
    /// @see <a href="https://github.com/spring-projects/spring-security/pull/17046">JwkSource Pull Request</a>
    @Bean
    public JwtDecoder jwtDecoder() throws MalformedURLException {
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