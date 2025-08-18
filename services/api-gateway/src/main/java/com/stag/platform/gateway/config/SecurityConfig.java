package com.stag.platform.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final String[] SWAGGER_URLS = {
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/swagger-config"
    };

    private static final String[] SERVICE_OPENAPI_URLS = {
        "/codelist-service/openapi.yaml",
        "/student-service/openapi.yaml",
        "/subject-service/openapi.yaml",
        "/user-service/openapi.yaml"
    };

    // TODO: make the actuator endpoints protected by an ADMIN role
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(ex -> ex
                .pathMatchers("/actuator/health").permitAll()
                .pathMatchers(SWAGGER_URLS).permitAll()
                .pathMatchers(SERVICE_OPENAPI_URLS).permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(jwt -> jwt.jwt(Customizer.withDefaults()))
            .build();
    }

}
