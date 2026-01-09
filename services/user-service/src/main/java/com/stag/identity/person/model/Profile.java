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
    public record Contact(
        String email,
        String phone,
        String mobile,
        String dataBox
    ) implements Serializable {

    }

    /// Academic and professional titles (prefix like "Ing." and suffix like "PhD.").
    public record Titles(
        String prefix,
        String suffix
    ) implements Serializable {

    }

    /// Birth place information with city name and country.
    public record BirthPlace(
        String city,
        String country
    ) implements Serializable {

    }

    /// Citizenship information with country name and citizenship qualifier.
    public record Citizenship(
        String country,
        String qualifier
    ) implements Serializable {

    }

}
