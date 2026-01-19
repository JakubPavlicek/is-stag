package com.stag.keycloak.authentication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

/**
 * Data Transfer Object (DTO) representing detailed user information in the IS/STAG system.
 * <p>
 * This record holds specific attributes of a user such as username, role, email,
 * and optional identifiers for students and teachers. It is part of the {@link IsStagUser} structure.
 * </p>
 *
 * @param userName The unique username of the user.
 * @param role The role assigned to the user within the system.
 * @param email The email address of the user.
 * @param studentId An {@link Optional} containing the student's identification number ("osCislo"), if applicable.
 * @param teacherId An {@link Optional} containing the teacher's identification number ("ucitIdno"), if applicable.
 * @author Jakub Pavlíček
 * @version 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IsStagUserDetails(
    @JsonProperty("userName") String userName,
    @JsonProperty("role") String role,
    @JsonProperty("email") String email,
    @JsonProperty("osCislo") Optional<String> studentId,
    @JsonProperty("ucitIdno") Optional<Long> teacherId
) {

}
