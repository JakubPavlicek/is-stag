package com.stag.platform.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;

/// **JWT Claims Extraction Filter**
///
/// Global filter that extracts claims from validated JWT tokens and forwards them as HTTP headers to downstream services.
/// This enables a centralized authentication model where the API Gateway validates tokens and downstream services
/// trust the gateway-provided headers.
///
/// **Extracted Headers**
///
/// | Header | JWT Claim | Description |
/// |--------|-----------|-------------|
/// | `X-Student-Id` | `studentId` | Student identifier (if user is a student) |
/// | `X-Teacher-Id` | `teacherId` | Teacher identifier (if user is a teacher) |
/// | `X-Roles` | `realm_access.roles` | Comma-separated list of user roles |
/// | `X-Email` | `email` | User email address |
///
/// This filter runs after JWT authentication and before routing to downstream services.
/// It is disabled for the `perftest` profile where security is bypassed.
///
/// @author Jakub Pavlicek
/// @version 1.0.0
@Profile("!perftest")
@Slf4j
@Component
public class JwtClaimsExtractionFilter implements GlobalFilter, Ordered {

    /// Header name for student identifier
    public static final String HEADER_STUDENT_ID = "X-Student-Id";

    /// Header name for teacher identifier
    public static final String HEADER_TEACHER_ID = "X-Teacher-Id";

    /// Header name for user roles
    public static final String HEADER_ROLES = "X-Roles";

    /// Header name for user email
    public static final String HEADER_EMAIL = "X-Email";

    /// Filter order - runs after a security filter chain (which has order -100)
    private static final int FILTER_ORDER = -50;

    /// Log format for added header
    private static final String ADDED_HEADER = "Added {} header: {}";

    /// Filters the request to extract JWT claims and add them as headers.
    ///
    /// If the request is authenticated with a JWT token, extracts relevant claims
    /// and adds them as custom headers to the downstream request.
    ///
    /// @param exchange the current server exchange
    /// @param chain the gateway filter chain
    /// @return Mono signaling completion of the filter
    @NonNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return exchange.getPrincipal()
                       .filter(JwtAuthenticationToken.class::isInstance)
                       .cast(JwtAuthenticationToken.class)
                       .map(jwt -> addClaimsHeaders(exchange, jwt))
                       .defaultIfEmpty(exchange)
                       .flatMap(chain::filter);
    }

    /// Returns the filter order.
    ///
    /// This filter runs after the security filter chain (-100) but before routing.
    ///
    /// @return the filter order
    @Override
    public int getOrder() {
        return FILTER_ORDER;
    }

    /// Adds JWT claims as HTTP headers to the downstream request.
    ///
    /// @param exchange the server web exchange
    /// @param jwt the JWT authentication token
    /// @return modified exchange with claims headers
    private ServerWebExchange addClaimsHeaders(ServerWebExchange exchange, JwtAuthenticationToken jwt) {
        ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate();

        // Extract student ID (if present)
        String studentId = jwt.getToken().getClaimAsString("studentId");
        if (studentId != null) {
            requestBuilder.header(HEADER_STUDENT_ID, studentId);
            log.debug(ADDED_HEADER, HEADER_STUDENT_ID, studentId);
        }

        // Extract teacher ID (if present)
        String teacherId = jwt.getToken().getClaimAsString("teacherId");
        if (teacherId != null) {
            requestBuilder.header(HEADER_TEACHER_ID, teacherId);
            log.debug(ADDED_HEADER, HEADER_TEACHER_ID, teacherId);
        }

        // Extract email
        String email = jwt.getToken().getClaimAsString("email");
        if (email != null) {
            requestBuilder.header(HEADER_EMAIL, email);
            log.debug(ADDED_HEADER, HEADER_EMAIL, email);
        }

        // Extract roles from realm_access.roles
        String roles = extractRoles(jwt);
        if (roles != null && !roles.isEmpty()) {
            requestBuilder.header(HEADER_ROLES, roles);
            log.debug(ADDED_HEADER, HEADER_ROLES, roles);
        }

        return exchange.mutate()
                       .request(requestBuilder.build())
                       .build();
    }

    /// Extracts roles from the Keycloak JWT realm_access claim.
    ///
    /// @param jwt the JWT authentication token
    /// @return comma-separated list of roles, or null if no roles found
    @SuppressWarnings("unchecked")
    private String extractRoles(JwtAuthenticationToken jwt) {
        Map<String, Object> realmAccess = jwt.getToken().getClaim("realm_access");
        if (realmAccess == null) {
            return null;
        }

        Collection<String> roles = (Collection<String>) realmAccess.get("roles");

        return String.join(",", roles);
    }

}
