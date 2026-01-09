package com.stag.platform.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/// **Production Security Configuration**
///
/// Configures comprehensive security for production environments with JWT-based authentication, CORS policies, and role-based access control.
/// This configuration is **disabled** for the `perftest` profile.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Profile("!perftest")
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /// Swagger UI and OpenAPI documentation URLs that require public access
    private static final String[] SWAGGER_URLS = {
        "/api/swagger-ui.html",
        "/api/swagger-ui/**",
        "/api/v3/api-docs/swagger-config",
        "/v3/api-docs/swagger-config"
    };

    /// Creates the main security filter chain with JWT authentication and authorization rules.
    ///
    /// @param http ServerHttpSecurity to configure
    /// @return Configured SecurityWebFilterChain with JWT and CORS support
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        return http
            // Disable CSRF for REST API (using JWT tokens)
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            // Configure CORS with custom settings
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Define authorization rules
            .authorizeExchange(ex -> ex
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers("/actuator/health/**").permitAll()
                .pathMatchers("/api/*/openapi.yaml").permitAll()
                .pathMatchers(SWAGGER_URLS).permitAll()
                .pathMatchers("/api/v1/addresses/**", "/api/v1/countries/**", "/api/v1/domains/**").permitAll()
                .pathMatchers("/actuator/**").hasRole("AD")
                .anyExchange().authenticated()
            )
            // Configure OAuth2 Resource Server with JWT
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
            ))
            .build();
    }

    /// Configures CORS (Cross-Origin Resource Sharing) settings.
    ///
    /// **CORS Configuration**
    ///
    /// - **Allowed Origins**: Production (`https://is-stag.cz`) and development (`http://localhost:5173`)
    /// - **Allowed Methods**: GET, POST, PUT, PATCH, DELETE, OPTIONS
    /// - **Allowed Headers**: All headers (`*`)
    /// - **Credentials**: Enabled (allows cookies and authorization headers)
    ///
    /// @return CorsConfigurationSource with production-ready CORS settings
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://is-stag.cz", "http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        // Apply CORS configuration to all paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    /// Creates a JWT authentication converter with custom role extraction.
    ///
    /// This converter uses `JwtRoleConverter` to extract Keycloak roles from the
    /// `realm_access.roles` claim and convert them to Spring Security authorities.
    ///
    /// @return ReactiveJwtAuthenticationConverter configured with a custom role converter
    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        
        // Set a custom converter to extract authorities from the JWT
        converter.setJwtGrantedAuthoritiesConverter(new JwtRoleConverter());
        
        return converter;
    }

}
