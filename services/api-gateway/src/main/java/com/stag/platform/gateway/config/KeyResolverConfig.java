package com.stag.platform.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

/// **Rate Limiting Key Resolver Configuration**
///
/// Configures a custom key resolver for Spring Cloud Gateway's rate limiting feature.
/// The resolver identifies users by their **email** from JWT tokens, with a fallback
/// to the **IP address** for unauthenticated requests.
///
/// **Resolution Strategy**
///
/// 1. **Authenticated Users**: Uses email from JWT token claim
/// 2. **Anonymous Users**: Falls back to client IP address
///
/// This ensures that rate limits are applied per-user for authenticated requests
/// and per-IP for unauthenticated requests.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Configuration
public class KeyResolverConfig {

    /// Creates a key resolver that identifies requests by email or IP address.
    ///
    /// The resolution logic:
    /// 1. Attempts to extract the principal as a `JwtAuthenticationToken`
    /// 2. Retrieves the `email` claim from the JWT token
    /// 3. If email is not present or the user is not authenticated, falls back to the IP address
    ///
    /// @return KeyResolver that returns the user email or IP address as the rate limit key
    @Bean
    KeyResolver emailKeyResolver() {
        return exchange -> exchange.getPrincipal()
                                   .cast(JwtAuthenticationToken.class)
                                   .flatMap(jwt -> Mono.justOrEmpty(jwt.getToken().getClaimAsString("email")))
                                   .switchIfEmpty(Mono.fromSupplier(() ->
                                       exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                                   ));
    }

}
