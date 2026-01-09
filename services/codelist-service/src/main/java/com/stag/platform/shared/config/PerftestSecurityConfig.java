package com.stag.platform.shared.config;

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
/// Provides permissive security for performance testing environments.
/// Only active with `perftest` profile.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Profile("perftest")
@Configuration
@EnableWebSecurity
public class PerftestSecurityConfig {

    /// Creates a permissive security filter chain for performance testing.
    ///
    /// @param http HttpSecurity configuration
    /// @return Security filter chain with all checks disabled
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
    }
}
