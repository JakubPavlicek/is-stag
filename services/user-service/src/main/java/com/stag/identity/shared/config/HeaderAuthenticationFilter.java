package com.stag.identity.shared.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

/// **Header Authentication Filter**
///
/// Servlet filter that extracts authentication information from HTTP headers set by the API Gateway.
/// This filter trusts headers from the gateway and creates a `GatewayAuthenticatedUser` principal
/// that is set in the Spring Security context.
///
/// **Expected Headers**
///
/// | Header | Description |
/// |--------|-------------|
/// | `X-Student-Id` | Student identifier (if user is a student) |
/// | `X-Teacher-Id` | Teacher identifier (if user is a teacher) |
/// | `X-Email` | User email address |
/// | `X-Roles` | Comma-separated list of user roles |
///
/// The filter only sets authentication if at least one identity header (`X-Student-Id` or `X-Teacher-Id`) is present.
/// This filter is disabled for the `perftest` profile where security is bypassed.
///
/// @author Jakub Pavlicek
/// @version 1.0.0
@Profile("!perftest")
@Slf4j
@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    /// Header name for student identifier
    public static final String HEADER_STUDENT_ID = "X-Student-Id";

    /// Header name for teacher identifier
    public static final String HEADER_TEACHER_ID = "X-Teacher-Id";

    /// Header name for user roles
    public static final String HEADER_ROLES = "X-Roles";

    /// Header name for user email
    public static final String HEADER_EMAIL = "X-Email";

    /// Extracts authentication from headers and sets the security context.
    ///
    /// @param request the HTTP request
    /// @param response the HTTP response
    /// @param filterChain the filter chain
    /// @throws ServletException if a servlet error occurs
    /// @throws IOException if an I/O error occurs
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String studentId = request.getHeader(HEADER_STUDENT_ID);
        String teacherId = request.getHeader(HEADER_TEACHER_ID);
        String email = request.getHeader(HEADER_EMAIL);
        String roles = request.getHeader(HEADER_ROLES);

        // Only authenticate if we have at least one identity header
        if (studentId != null || teacherId != null) {
            Collection<GrantedAuthority> authorities = parseRoles(roles);

            GatewayAuthenticatedUser user = new GatewayAuthenticatedUser(
                studentId,
                teacherId,
                email,
                authorities
            );

            GatewayAuthenticationToken authentication = new GatewayAuthenticationToken(user, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Authenticated user from gateway headers: studentId={}, teacherId={}, roles={}",
                studentId, teacherId, roles);
        }

        filterChain.doFilter(request, response);
    }

    /// Parses the comma-separated roles string into granted authorities.
    ///
    /// @param roles comma-separated list of roles
    /// @return collection of granted authorities with ROLE_ prefix
    private Collection<GrantedAuthority> parseRoles(String roles) {
        if (roles == null || roles.isBlank()) {
            return Collections.emptyList();
        }

        return Arrays.stream(roles.split(","))
                     .map(String::trim)
                     .filter(role -> !role.isEmpty())
                     .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                     .collect(Collectors.toList());
    }

}
