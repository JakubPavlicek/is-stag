package com.stag.identity.person.model;

import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;

/// **Education Model**
///
/// Person education history supporting Czech and foreign high schools.
/// Includes localized field of study names and school address details.
///
/// @param highSchool the Czech high school education
/// @param foreignHighSchool the foreign high school education
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Builder
public record Education(
    HighSchool highSchool,
    ForeignHighSchool foreignHighSchool
) implements Serializable {

    /// Czech high school with full address and localized field of study.
    ///
    /// @param name the name
    /// @param fieldOfStudy the field of study
    /// @param graduationDate the graduation date
    /// @param address the address
    @Builder
    public record HighSchool(
        String name,
        String fieldOfStudy,
        LocalDate graduationDate,
        HighSchoolAddress address
    ) implements Serializable {

    }

    /// Foreign high school with simplified location and field of study.
    ///
    /// @param name the name
    /// @param location the location
    /// @param fieldOfStudy the field of study
    @Builder
    public record ForeignHighSchool(
        String name,
        String location,
        String fieldOfStudy
    ) implements Serializable {

    }

    /// Czech high school address with full structure including district.
    ///
    /// @param street the street
    /// @param zipCode the zip code
    /// @param municipality the municipality
    /// @param district the district
    /// @param country the country
    @Builder
    public record HighSchoolAddress(
        String street,
        String zipCode,
        String municipality,
        String district,
        String country
    ) implements Serializable {

    }

}
