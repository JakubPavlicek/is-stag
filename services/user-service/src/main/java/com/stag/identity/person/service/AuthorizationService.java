package com.stag.identity.person.service;

import com.stag.identity.shared.grpc.client.StudentClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/// **Authorization Service**
///
/// Security authorization logic for person data access control. Validates
/// that students can only access their own profile data by comparing
/// student ID to person ID ownership.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@Service
public class AuthorizationService {

    /// gRPC Student Client
    private final StudentClient studentClient;

    /// Checks if the user is a student and owns the requested person profile.
    /// Used in @PreAuthorize expressions to enforce data access restrictions.
    ///
    /// @param isStudent whether a user has a student role
    /// @param studentId the student identifier from JWT claims
    /// @param personId the requested person identifier
    /// @return true if a student owns the person profile, false otherwise
    public boolean isStudentAndOwner(boolean isStudent, String studentId, Integer personId) {
        if (!isStudent) {
            log.debug("User is not a student");
            return false;
        }

        Integer studentPersonId = studentClient.getStudentPersonId(studentId);
        return studentPersonId.equals(personId);
    }

}
