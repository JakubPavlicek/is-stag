package com.stag.academics.shared.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/// **Security Configuration**
///
/// Production security configuration for the Student service.
/// The API Gateway validates JWT tokens and forwards user identity as HTTP headers.
/// This service trusts the gateway and extracts authentication from headers via `HeaderAuthenticationFilter`.
///
/// **Authentication Flow**
///
/// 1. API Gateway validates JWT token from Keycloak
/// 2. Gateway extracts claims and sets `X-Student-Id`, `X-Teacher-Id`, `X-Roles`, `X-Email` headers
/// 3. Gateway strips the `Authorization` header before forwarding
/// 4. This service reads headers via `HeaderAuthenticationFilter` and populates `SecurityContext`
///
/// Active for all profiles except 'perftest'.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Profile("!perftest")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /// Swagger UI endpoint patterns for public access
    private static final String[] SWAGGER_URLS = {
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/swagger-config"
    };

    /// Header authentication filter
    private final HeaderAuthenticationFilter headerAuthenticationFilter;

    /// Configures the security filter chain with header-based authentication and authorization rules.
    ///
    /// Permits public access to health checks, OpenAPI docs, and Swagger UI.
    /// Requires authentication for all other endpoints and 'AD' role for actuator.
    ///
    /// @param http the HTTP security configuration
    /// @return configured security filter chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/openapi.yaml").permitAll()
                .requestMatchers(SWAGGER_URLS).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(headerAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
    }

}
