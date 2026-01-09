package com.stag.academics.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.ExpressionJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

/// **Security Configuration**
///
/// Spring Security configuration for production environments.
/// Configures OAuth2 resource server with JWT authentication,
/// role-based access control from Keycloak realm_access.roles, and endpoint authorization rules.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Profile("!perftest")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /// Swagger public URLs.
    private static final String[] SWAGGER_URLS = {
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/swagger-config"
    };

    /// Configures the security filter chain with OAuth2 and JWT authentication.
    ///
    /// @param http the HTTP security builder
    /// @return configured security filter chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf(Customizer.withDefaults())
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

    /// Creates converter for extracting granted authorities from JWT realm roles.
    ///
    /// @return expression-based JWT granted authorities converter
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

    /// Creates JWT authentication converter with custom authorities converter.
    ///
    /// @return JWT authentication converter
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthConverter = new JwtAuthenticationConverter();
        jwtAuthConverter.setJwtGrantedAuthoritiesConverter(expressionConverter());

        return jwtAuthConverter;
    }

}
