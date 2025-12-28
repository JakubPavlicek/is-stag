package com.stag.identity.shared.config;

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
/// Test/QA environment security configuration with all security disabled.
/// Permits all requests without authentication for simplified testing and
/// development workflows. Activated only with "qa" Spring profile.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Profile("qa")
@Configuration
@EnableWebSecurity
public class QaSecurityConfig {

    /// Configures a permissive security filter chain for QA environment.
    /// Disables CSRF and authentication for all endpoints.
    ///
    /// @param http the HTTP security configuration
    /// @return configured security filter chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize ->
                authorize.anyRequest().permitAll()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
    }
}
