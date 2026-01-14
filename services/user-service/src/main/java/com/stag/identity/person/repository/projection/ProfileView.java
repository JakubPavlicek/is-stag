package com.stag.identity.person.repository.projection;

import java.time.LocalDate;

/// **Profile View Projection**
///
/// Comprehensive view of a person's profile containing personal identification, contact information, birth details, citizenship, and document data.
/// Used for full profile retrieval.
///
/// @param id the person ID
/// @param firstName the first name
/// @param lastName the last name
/// @param birthSurname the birth surname
/// @param email the email address
/// @param phone the phone number
/// @param mobile the mobile phone number
/// @param dataBox the data box ID
/// @param titlePrefix the title prefix
/// @param titleSuffix the title suffix
/// @param birthNumber the birth number
/// @param birthDate the birthdate
/// @param birthCountryId the birth country ID
/// @param birthPlace the birthplace
/// @param citizenshipCountryId the citizenship country ID
/// @param citizenshipQualification the citizenship qualification
/// @param passportNumber the passport number
/// @param gender the gender
/// @param maritalStatus the marital status
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
