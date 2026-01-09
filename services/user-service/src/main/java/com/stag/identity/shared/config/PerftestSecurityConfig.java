package com.stag.identity.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/// **Performance Test Security Configuration**
///
/// Performance testing environment security configuration with all security disabled.
/// Permits all requests without authentication for simplified k6 load testing workflows.
/// Activated only with "perftest" Spring profile.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Profile("perftest")
@Configuration
@EnableWebSecurity
public class PerftestSecurityConfig {

    /// Configures a permissive security filter chain for performance testing environment.
    /// Disables CSRF and authentication for all endpoints.
    ///
    /// @param http the HTTP security configuration
    /// @return configured security filter chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
    }
}
