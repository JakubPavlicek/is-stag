package com.stag.academics.shared.config;

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
/// Minimal security configuration for performance testing environments.
/// Disables authentication and authorization for K6 load testing.
/// Active only when the 'perftest' profile is enabled.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Profile("perftest")
@Configuration
@EnableWebSecurity
public class PerftestSecurityConfig {

    /// Configures a permissive security filter chain for performance testing environment.
    ///
    /// Disables CSRF protection and permits all requests without authentication.
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
