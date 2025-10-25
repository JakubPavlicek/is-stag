package com.stag.platform.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ExpressionJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] SWAGGER_URLS = {
        "/api/swagger-ui.html",
        "/api/swagger-ui/**",
        "/v3/api-docs/swagger-config"
    };

    // TODO: make the actuator endpoints protected by an ADMIN role
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(Customizer.withDefaults())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/actuator/health/**").permitAll()
                .requestMatchers("/api/*/openapi.yaml").permitAll()
                .requestMatchers(SWAGGER_URLS).permitAll()
                .requestMatchers("/api/v1/addresses/**", "/api/v1/countries/**", "/api/v1/domains/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
            ))
            .build();
    }

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

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthConverter = new JwtAuthenticationConverter();
        jwtAuthConverter.setJwtGrantedAuthoritiesConverter(expressionConverter());

        return jwtAuthConverter;
    }

}
