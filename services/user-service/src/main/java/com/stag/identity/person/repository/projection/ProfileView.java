package com.stag.identity.person.repository.projection;

import java.time.LocalDate;

/// **Profile View Projection**
///
/// Comprehensive view of a person's profile containing personal identification,
/// contact information, birth details, citizenship, and document data.
/// Used for full profile retrieval.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
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
