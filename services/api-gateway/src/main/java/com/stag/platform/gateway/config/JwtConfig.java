package com.stag.platform.gateway.config;

import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
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
import java.util.List;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Configuration
public class JwtConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    /// @see <a href="https://connect2id.com/products/nimbus-jose-jwt/examples/enhanced-jwk-retrieval">JWKs Retrieval</a>
    @Bean
    public JwtDecoder jwtDecoder() throws KeySourceException, MalformedURLException {
        URL jwkSetURL = URI.create(jwkSetUri).toURL();

        // Build the Nimbus-enhanced JWK source with caching and retry capabilities
        JWKSource<SecurityContext> jwkSource = JWKSourceBuilder.create(jwkSetURL)
                                                               .cache(MINUTES.toMillis(15), SECONDS.toMillis(10))
                                                               .refreshAheadCache(MINUTES.toMillis(1), true)
                                                               .outageTolerant(HOURS.toMillis(4))
                                                               .retrying(true)
                                                               .build();

        // Selector that matches RSA signing keys
        JWKSelector selector = new JWKSelector(new JWKMatcher.Builder()
            .keyType(KeyType.RSA)
            .keyUse(KeyUse.SIGNATURE)
            .build()
        );

        // Trigger the JWKs retrieval
        List<JWK> jwks = jwkSource.get(selector, null);

        log.info("Loading {} JWKs on startup", jwks.size());

        // Create the NimbusJwtDecoder with the JWK source
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

}