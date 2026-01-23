package com.stag.platform.shared.config;

import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;

/// **Gateway Authenticated User**
///
/// Represents an authenticated user whose identity was extracted from gateway headers.
/// This record holds the user's identity claims that were originally extracted from the JWT token
/// at the API Gateway and forwarded as HTTP headers.
///
/// **Usage**
///
/// This principal is created by `HeaderAuthenticationFilter` from the following headers:
/// - `X-Student-Id` - Student identifier (if user is a student)
/// - `X-Teacher-Id` - Teacher identifier (if user is a teacher)
/// - `X-Email` - User email address
/// - `X-Roles` - Comma-separated list of user roles
///
/// @param studentId the student identifier, or null if user is not a student
/// @param teacherId the teacher identifier, or null if user is not a teacher
/// @param email the user email address
/// @param authorities the granted authorities derived from roles
///
/// @author Jakub Pavlicek
/// @version 1.0.0
public record GatewayAuthenticatedUser(
    String studentId,
    String teacherId,
    String email,
    Collection<? extends GrantedAuthority> authorities
) implements Principal {

    /// Returns the principal name.
    ///
    /// Returns the teacher ID if present, otherwise the student ID, otherwise the email.
    ///
    /// @return the principal name
    @Override
    public String getName() {
        if (teacherId != null) {
            return teacherId;
        }
        if (studentId != null) {
            return studentId;
        }
        return email;
    }

    /// Checks if the user is a student.
    ///
    /// @return true if the user has a student ID
    public boolean isStudent() {
        return studentId != null;
    }

}
