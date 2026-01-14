package com.stag.identity.person.model;

import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/// **Profile Model**
///
/// Complete person profile containing personal information, contact details, citizenship, and associated student IDs.
/// Used for full profile retrieval with localized codelist meanings.
///
/// @param personId the person ID
/// @param studentIds the list of student IDs associated with the person
/// @param firstName the first name
/// @param lastName the last name
/// @param birthSurname the birth surname
/// @param contact contact information
/// @param titles academic and professional titles
/// @param birthNumber the birth number
/// @param birthDate the birthdate
/// @param birthPlace birthplace information
/// @param citizenship citizenship information
/// @param passportNumber the passport number
/// @param gender the gender
/// @param maritalStatus the marital status
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Builder
public record Profile(
    Integer personId,
    List<String> studentIds,
    String firstName,
    String lastName,
    String birthSurname,
    Contact contact,
    Titles titles,
    String birthNumber,
    LocalDate birthDate,
    BirthPlace birthPlace,
    Citizenship citizenship,
    String passportNumber,
    String gender,
    String maritalStatus
) implements Serializable {

    /// Contact information including email, phone, mobile, and Czech data box.
    ///
    /// @param email the email
    /// @param phone the phone
    /// @param mobile the mobile
    /// @param dataBox the data box ID
    public record Contact(
        String email,
        String phone,
        String mobile,
        String dataBox
    ) implements Serializable {

    }

    /// Academic and professional titles (prefix like "Ing." and suffix like "PhD.").
    ///
    /// @param prefix the prefix
    /// @param suffix the suffix
    public record Titles(
        String prefix,
        String suffix
    ) implements Serializable {

    }

    /// Birthplace information with city name and country.
    ///
    /// @param city the birth city
    /// @param country the birth country
    public record BirthPlace(
        String city,
        String country
    ) implements Serializable {

    }

    /// Citizenship information with country name and citizenship qualifier.
    ///
    /// @param country the country
    /// @param qualifier the qualifier
    public record Citizenship(
        String country,
        String qualifier
    ) implements Serializable {

    }

}
