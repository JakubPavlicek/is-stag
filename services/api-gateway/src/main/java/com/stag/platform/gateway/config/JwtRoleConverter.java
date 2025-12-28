package com.stag.platform.gateway.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.Map;

/// **JWT Role Converter for Keycloak Integration**
///
/// Extracts user roles from Keycloak JWT tokens and converts them to Spring Security authorities.
/// This converter reads roles from the `realm_access.roles` claim and prefixes them with `ROLE_`
/// for compatibility with Spring Security's role-based authorization.
///
/// **JWT Structure Expected**
///
/// ```json
/// {
///   "realm_access": {
///     "roles": ["ST", "AD", "VY"]
///   }
/// }
/// ```
///
/// **Output**
///
/// The converter produces Spring Security authorities in the format: `ROLE_ST`, `ROLE_AD`, etc.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public class JwtRoleConverter implements Converter<Jwt, Flux<GrantedAuthority>> {

    /// Converts JWT roles to Spring Security granted authorities.
    ///
    /// @param jwt The JWT token to extract roles from
    /// @return Flux of `GrantedAuthority` with `ROLE_` prefix, or empty flux if no roles found
    @Override
    @SuppressWarnings("unchecked")
    public Flux<GrantedAuthority> convert(Jwt jwt) {
        // Extract all claims from the JWT token
        Map<String, Object> claims = jwt.getClaims();
        
        // Get the realm_access claim (Keycloak-specific structure)
        Map<String, Object> realmAccess = (Map<String, Object>) claims.get("realm_access");

        // Return empty if realm_access is not present
        if (realmAccess == null || realmAccess.isEmpty()) {
            return Flux.empty();
        }

        // Extract the roles array from realm_access
        Collection<String> roles = (Collection<String>) realmAccess.get("roles");

        // Return empty if no roles are defined
        if (roles == null || roles.isEmpty()) {
            return Flux.empty();
        }

        // Convert each role to a GrantedAuthority with ROLE_ prefix
        return Flux.fromIterable(roles)
                   .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName));
    }

}