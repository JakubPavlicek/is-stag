package com.stag.academics.shared.config;

import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/// **Gateway Authentication Token**
///
/// Spring Security authentication token that holds the gateway-authenticated user.
@EqualsAndHashCode(callSuper = true)
final class GatewayAuthenticationToken extends AbstractAuthenticationToken {

    private final transient GatewayAuthenticatedUser principal;

    GatewayAuthenticationToken(
        GatewayAuthenticatedUser principal,
        Collection<? extends GrantedAuthority> authorities
    ) {
        super(authorities);
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null; // No credentials - we trust the gateway
    }

    @Override
    public GatewayAuthenticatedUser getPrincipal() {
        return principal;
    }

}
