package com.stag.platform.gateway.config;

import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

@Configuration
public class KeyResolverConfig {

    @Bean
    KeyResolver emailKeyResolver() {
        return exchange -> {
            Mono<@NonNull JwtAuthenticationToken> jwtAuth = exchange.getPrincipal().cast(JwtAuthenticationToken.class);
            return jwtAuth.flatMap(jwt -> Mono.justOrEmpty(jwt.getToken().getClaimAsString("email")));
        };
    }

}
