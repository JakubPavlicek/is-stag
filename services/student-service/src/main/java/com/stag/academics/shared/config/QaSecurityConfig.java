package com.stag.academics.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/// **QA Security Configuration**
///
/// Minimal security configuration for QA/testing environments.
/// Disables authentication and authorization for easier testing.
/// Active only when the 'qa' profile is enabled.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Profile("qa")
@Configuration
@EnableWebSecurity
public class QaSecurityConfig {

    /// Configures a permissive security filter chain for QA environment.
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
