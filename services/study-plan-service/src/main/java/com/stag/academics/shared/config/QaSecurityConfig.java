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
/// Simplified security configuration for QA/testing environments. Disables CSRF
/// and permits all requests without authentication for easier testing.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Profile("qa")
@Configuration
@EnableWebSecurity
public class QaSecurityConfig {

    /// Configures a permissive security filter chain for QA environment.
    ///
    /// @param http the HTTP security builder
    /// @return configured security filter chain with all access permitted
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
    }
}
