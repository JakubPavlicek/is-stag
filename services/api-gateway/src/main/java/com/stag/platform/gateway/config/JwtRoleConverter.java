package com.stag.platform.gateway.config;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.Map;

public class JwtRoleConverter implements Converter<@NonNull Jwt, @NonNull Flux<GrantedAuthority>> {

    @Override
    @SuppressWarnings("unchecked")
    public Flux<@Nullable GrantedAuthority> convert(@NonNull Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();
        Map<String, Object> realmAccess = (Map<String, Object>) claims.get("realm_access");

        if (realmAccess == null || realmAccess.isEmpty()) {
            return Flux.empty();
        }

        Collection<String> roles = (Collection<String>) realmAccess.get("roles");

        if (roles == null || roles.isEmpty()) {
            return Flux.empty();
        }

        return Flux.fromIterable(roles)
                   .map(roleName -> new SimpleGrantedAuthority("ROLE_" + roleName));
    }

}