package com.stag.identity.person.repository.projection;

import java.time.LocalDate;

public record ProfileView(
    Integer id,
    String firstName,
    String lastName,
    String birthSurname,
    String email,
    String phone,
    String mobile,
    String dataBox,
    String titlePrefix,
    String titleSuffix,
    String birthNumber,
    LocalDate birthDate,
    Integer birthCountryId,
    String birthPlace,
    Integer citizenshipCountryId,
    String citizenshipQualification,
    String passportNumber,
    String gender,
    String maritalStatus
) {

}
