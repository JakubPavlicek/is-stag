package com.stag.identity.user.repository.projection;

import java.time.LocalDate;

public record PersonProfileProjection(
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
