package com.stag.identity.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.ExpressionJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/// **Security Configuration**
///
/// Production security configuration with OAuth2 resource server and JWT authentication.
/// Extracts roles from Keycloak JWT tokens (realm_access.roles claim) and enforces method-level security via @PreAuthorize.
/// Public endpoints include health checks, OpenAPI docs, and Swagger UI.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Profile("!perftest")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /// Swagger UI and OpenAPI documentation public endpoints
    private static final String[] SWAGGER_URLS = {
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/swagger-config"
    };

    /// Configures a Spring Security filter chain with JWT authentication.
    /// Enables CSRF protection, OAuth2 resource server, and stateless session management.
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
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
            ))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .build();
    }

    /// Creates JWT authorities converter extracting roles from Keycloak token.
    /// Reads realm_access.roles claim and prefixes with "ROLE_" for Spring Security.
    ///
    /// @return expression-based JWT authorities converter
    @Bean
    public ExpressionJwtGrantedAuthoritiesConverter expressionConverter() {
        // Extract roles from the realm_access.roles claim and prefix with "ROLE_"
        ExpressionJwtGrantedAuthoritiesConverter expressionConverter =
            new ExpressionJwtGrantedAuthoritiesConverter(
                new SpelExpressionParser().parseRaw("[realm_access][roles]")
            );
        expressionConverter.setAuthorityPrefix("ROLE_");

        return expressionConverter;
    }

    /// Configures JWT authentication converter with custom authorities' extraction.
    ///
    /// @return JWT authentication converter
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthConverter = new JwtAuthenticationConverter();
        jwtAuthConverter.setJwtGrantedAuthoritiesConverter(expressionConverter());

        return jwtAuthConverter;
    }

}
