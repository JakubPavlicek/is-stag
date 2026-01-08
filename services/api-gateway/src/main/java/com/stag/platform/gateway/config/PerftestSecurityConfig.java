package com.stag.platform.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/// **Performance Test Security Configuration**
///
/// Provides a **permissive** security configuration for performance testing environments.
/// This configuration is **only active** when the `perftest` Spring profile is enabled.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Profile("perftest")
@Configuration
@EnableWebFluxSecurity
public class PerftestSecurityConfig {

    /// Creates a permissive security filter chain for performance testing environments.
    ///
    /// @param http ServerHttpSecurity to configure
    /// @return Configured SecurityWebFilterChain with all security disabled
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(authorize -> authorize.anyExchange().permitAll())
            .build();
    }
}
