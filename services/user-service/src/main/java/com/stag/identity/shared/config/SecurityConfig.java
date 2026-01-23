package com.stag.identity.shared.config;

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
/// Production security configuration with header-based authentication.
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
/// Public endpoints include health checks, OpenAPI docs, and Swagger UI.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Profile("!perftest")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /// Swagger UI and OpenAPI documentation public endpoints
    private static final String[] SWAGGER_URLS = {
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/swagger-config"
    };

    /// Header authentication filter
    private final HeaderAuthenticationFilter headerAuthenticationFilter;

    /// Configures a Spring Security filter chain with header-based authentication.
    /// Enables method-level security via @PreAuthorize and stateless session management.
    ///
    /// @param http the HTTP security configuration
    /// @return configured security filter chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/actuator/health/**").permitAll()
                .requestMatchers("/openapi.yaml").permitAll()
                .requestMatchers(SWAGGER_URLS).permitAll()
                .requestMatchers("/actuator/**").hasRole("AD")
                .anyRequest().authenticated()
            )
            .addFilterBefore(headerAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
    }

}
