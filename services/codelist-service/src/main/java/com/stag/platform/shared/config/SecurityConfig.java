package com.stag.platform.shared.config;

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

/// **Production Security Configuration**
///
/// Configures JWT-based authentication and role-based authorization for production environments.
/// Disabled for `perftest` profile.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Profile("!perftest")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /// Swagger UI and OpenAPI documentation URLs
    private static final String[] SWAGGER_URLS = {
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/swagger-config"
    };

    /// Header authentication filter
    private final HeaderAuthenticationFilter headerAuthenticationFilter;

    /// Configures the security filter chain with JWT and authorization rules.
    ///
    /// @param http HttpSecurity configuration
    /// @return Configured security filter chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/openapi.yaml").permitAll()
                .requestMatchers(SWAGGER_URLS).permitAll()
                .requestMatchers("/api/v1/addresses/**", "/api/v1/countries/**", "/api/v1/domains/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(headerAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
    }

}
