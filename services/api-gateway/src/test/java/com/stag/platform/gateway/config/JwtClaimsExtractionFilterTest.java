package com.stag.platform.gateway.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtClaimsExtractionFilterTest {

    private final JwtClaimsExtractionFilter filter = new JwtClaimsExtractionFilter();

    @Test
    @DisplayName("should extract student ID and add as header")
    void filter_WithStudentId_AddsStudentIdHeader() {
        Jwt jwt = createJwtWithClaims(Map.of(
            "studentId", "S12345",
            "email", "student@example.com",
            "realm_access", Map.of("roles", List.of("ST"))
        ));

        JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request)
            .mutate().principal(Mono.just(authToken)).build();

        when(chain.filter(any(ServerWebExchange.class))).thenAnswer(invocation -> {
            ServerWebExchange modifiedExchange = invocation.getArgument(0);
            HttpHeaders headers = modifiedExchange.getRequest().getHeaders();

            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_STUDENT_ID)).isEqualTo("S12345");
            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_TEACHER_ID)).isNull();
            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_EMAIL)).isEqualTo("student@example.com");
            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_ROLES)).isEqualTo("ST");

            return Mono.empty();
        });

        StepVerifier.create(filter.filter(exchange, chain))
                    .verifyComplete();
    }

    @Test
    @DisplayName("should extract teacher ID and add as header")
    void filter_WithTeacherId_AddsTeacherIdHeader() {
        Jwt jwt = createJwtWithClaims(Map.of(
            "teacherId", "T99999",
            "email", "teacher@example.com",
            "realm_access", Map.of("roles", List.of("VY", "AD"))
        ));

        JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request)
            .mutate().principal(Mono.just(authToken)).build();

        when(chain.filter(any(ServerWebExchange.class))).thenAnswer(invocation -> {
            ServerWebExchange modifiedExchange = invocation.getArgument(0);
            HttpHeaders headers = modifiedExchange.getRequest().getHeaders();

            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_STUDENT_ID)).isNull();
            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_TEACHER_ID)).isEqualTo("T99999");
            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_EMAIL)).isEqualTo("teacher@example.com");
            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_ROLES)).isEqualTo("VY,AD");

            return Mono.empty();
        });

        StepVerifier.create(filter.filter(exchange, chain))
                    .verifyComplete();
    }

    @Test
    @DisplayName("should extract multiple roles as comma-separated string")
    void filter_WithMultipleRoles_AddsCommaSeparatedRoles() {
        Jwt jwt = createJwtWithClaims(Map.of(
            "studentId", "S12345",
            "email", "user@example.com",
            "realm_access", Map.of("roles", List.of("ST", "AD", "VY"))
        ));

        JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request)
            .mutate().principal(Mono.just(authToken)).build();

        when(chain.filter(any(ServerWebExchange.class))).thenAnswer(invocation -> {
            ServerWebExchange modifiedExchange = invocation.getArgument(0);
            HttpHeaders headers = modifiedExchange.getRequest().getHeaders();

            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_ROLES)).isEqualTo("ST,AD,VY");

            return Mono.empty();
        });

        StepVerifier.create(filter.filter(exchange, chain))
                    .verifyComplete();
    }

    @Test
    @DisplayName("should continue without headers when no JWT authentication")
    void filter_WithoutAuthentication_ContinuesWithoutHeaders() {
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        // No principal set - simulates unauthenticated request

        when(chain.filter(any(ServerWebExchange.class))).thenAnswer(invocation -> {
            ServerWebExchange modifiedExchange = invocation.getArgument(0);
            HttpHeaders headers = modifiedExchange.getRequest().getHeaders();

            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_STUDENT_ID)).isNull();
            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_TEACHER_ID)).isNull();
            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_EMAIL)).isNull();
            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_ROLES)).isNull();

            return Mono.empty();
        });

        StepVerifier.create(filter.filter(exchange, chain))
                    .verifyComplete();
    }

    @Test
    @DisplayName("should handle missing realm_access claim")
    void filter_WithoutRealmAccess_AddsOtherHeadersWithoutRoles() {
        Jwt jwt = createJwtWithClaims(Map.of(
            "studentId", "S12345",
            "email", "student@example.com"
        ));

        JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request)
            .mutate().principal(Mono.just(authToken)).build();

        when(chain.filter(any(ServerWebExchange.class))).thenAnswer(invocation -> {
            ServerWebExchange modifiedExchange = invocation.getArgument(0);
            HttpHeaders headers = modifiedExchange.getRequest().getHeaders();

            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_STUDENT_ID)).isEqualTo("S12345");
            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_EMAIL)).isEqualTo("student@example.com");
            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_ROLES)).isNull();

            return Mono.empty();
        });

        StepVerifier.create(filter.filter(exchange, chain))
                    .verifyComplete();
    }

    @Test
    @DisplayName("should handle empty roles list")
    void filter_WithEmptyRoles_DoesNotAddRolesHeader() {
        Jwt jwt = createJwtWithClaims(Map.of(
            "studentId", "S12345",
            "email", "student@example.com",
            "realm_access", Map.of("roles", List.of())
        ));

        JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request)
            .mutate().principal(Mono.just(authToken)).build();

        when(chain.filter(any(ServerWebExchange.class))).thenAnswer(invocation -> {
            ServerWebExchange modifiedExchange = invocation.getArgument(0);
            HttpHeaders headers = modifiedExchange.getRequest().getHeaders();

            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_STUDENT_ID)).isEqualTo("S12345");
            // Empty roles should result in empty string, which should not be added
            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_ROLES)).isNull();

            return Mono.empty();
        });

        StepVerifier.create(filter.filter(exchange, chain))
                    .verifyComplete();
    }

    @Test
    @DisplayName("should handle missing email claim")
    void filter_WithoutEmail_AddsOtherHeadersWithoutEmail() {
        Jwt jwt = createJwtWithClaims(Map.of(
            "studentId", "S12345",
            "realm_access", Map.of("roles", List.of("ST"))
        ));

        JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt);
        GatewayFilterChain chain = mock(GatewayFilterChain.class);

        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request)
            .mutate().principal(Mono.just(authToken)).build();

        when(chain.filter(any(ServerWebExchange.class))).thenAnswer(invocation -> {
            ServerWebExchange modifiedExchange = invocation.getArgument(0);
            HttpHeaders headers = modifiedExchange.getRequest().getHeaders();

            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_STUDENT_ID)).isEqualTo("S12345");
            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_EMAIL)).isNull();
            assertThat(headers.getFirst(JwtClaimsExtractionFilter.HEADER_ROLES)).isEqualTo("ST");

            return Mono.empty();
        });

        StepVerifier.create(filter.filter(exchange, chain))
                    .verifyComplete();
    }

    @Test
    @DisplayName("should return correct filter order")
    void getOrder_ReturnsCorrectOrder() {
        assertThat(filter.getOrder()).isEqualTo(-50);
    }

    /// Creates a JWT with the specified claims.
    ///
    /// @param claims map of claims to add to the JWT
    /// @return a JWT with the specified claims
    private Jwt createJwtWithClaims(Map<String, Object> claims) {
        return Jwt.withTokenValue("token")
                  .header("alg", "RS256")
                  .claims(c -> c.putAll(claims))
                  .claim("sub", "user-id")
                  .issuedAt(Instant.now())
                  .expiresAt(Instant.now().plusSeconds(3600))
                  .build();
    }

}
