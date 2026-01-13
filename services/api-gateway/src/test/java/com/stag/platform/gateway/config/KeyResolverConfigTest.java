package com.stag.platform.gateway.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.InetSocketAddress;
import java.time.Instant;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KeyResolverConfigTest {

    private final KeyResolverConfig config = new KeyResolverConfig();
    private final KeyResolver keyResolver = config.emailKeyResolver();

    @Test
    @DisplayName("should resolve key from JWT email claim for authenticated user")
    void resolve_AuthenticatedUserWithEmail_ReturnsEmail() {
        Jwt jwt = Jwt.withTokenValue("token")
                     .header("alg", "none")
                     .claim("email", "user@example.com")
                     .claim("sub", "user")
                     .issuedAt(Instant.now())
                     .expiresAt(Instant.now().plusSeconds(3600))
                     .build();
        JwtAuthenticationToken principal = new JwtAuthenticationToken(jwt);

        ServerWebExchange exchange = mock(ServerWebExchange.class);
        when(exchange.getPrincipal()).thenReturn(Mono.just(principal));

        StepVerifier.create(keyResolver.resolve(exchange))
                    .expectNext("user@example.com")
                    .verifyComplete();
    }

    @Test
    @DisplayName("should fall back to IP address when JWT has no email claim")
    void resolve_AuthenticatedUserWithoutEmail_ReturnsIp() {
        Jwt jwt = Jwt.withTokenValue("token")
                     .header("alg", "none")
                     .claim("sub", "user")
                     .issuedAt(Instant.now())
                     .expiresAt(Instant.now().plusSeconds(3600))
                     .build();
        JwtAuthenticationToken principal = new JwtAuthenticationToken(jwt);

        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        InetSocketAddress remoteAddress = new InetSocketAddress("192.168.1.1", 12345);

        when(exchange.getPrincipal()).thenReturn(Mono.just(principal));
        when(exchange.getRequest()).thenReturn(request);
        when(request.getRemoteAddress()).thenReturn(remoteAddress);

        StepVerifier.create(keyResolver.resolve(exchange))
                    .expectNext("192.168.1.1")
                    .verifyComplete();
    }

    @Test
    @DisplayName("should fall back to IP address when user is not authenticated")
    void resolve_UnauthenticatedUser_ReturnsIp() {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        InetSocketAddress remoteAddress = new InetSocketAddress("10.0.0.1", 8080);

        when(exchange.getPrincipal()).thenReturn(Mono.empty());
        when(exchange.getRequest()).thenReturn(request);
        when(request.getRemoteAddress()).thenReturn(remoteAddress);

        StepVerifier.create(keyResolver.resolve(exchange))
                    .expectNext("10.0.0.1")
                    .verifyComplete();
    }
}