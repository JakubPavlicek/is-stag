package com.stag.identity.user.domain.person.model;

import lombok.Builder;

import java.time.LocalDate;

/**
 * A read-only DTO representing the data needed for a person's profile view.
 * This is a projection, not a domain object.
 */
@Builder
public record PersonProfile(
    Integer id,
    String firstName,
    String lastName,
    String birthName,
    String email,
    String phone,
    String mobile,
    String titlePrefix,
    String titleSuffix,
    String birthNumber,
    LocalDate birthDate,
    Integer birthCountryId,
    String birthPlace,
    Integer residenceCountryId,
    String citizenshipQualification,
    String passportNumber,
    String gender,
    String maritalStatus
) {

}