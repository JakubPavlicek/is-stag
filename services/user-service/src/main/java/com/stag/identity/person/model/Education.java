package com.stag.identity.person.model;

import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDate;

/// **Education Model**
///
/// Person education history supporting Czech and foreign high schools.
/// Includes localized field of study names and school address details.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Builder
public record Education(
    HighSchool highSchool,
    ForeignHighSchool foreignHighSchool
) implements Serializable {

    /// Czech high school with full address and localized field of study.
    @Builder
    public record HighSchool(
        String name,
        String fieldOfStudy,
        LocalDate graduationDate,
        HighSchoolAddress address
    ) implements Serializable {

    }

    /// Foreign high school with simplified location and field of study.
    @Builder
    public record ForeignHighSchool(
        String name,
        String location,
        String fieldOfStudy
    ) implements Serializable {

    }

    /// Czech high school address with full structure including district.
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
