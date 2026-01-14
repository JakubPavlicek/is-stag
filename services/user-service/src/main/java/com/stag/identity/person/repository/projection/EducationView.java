package com.stag.identity.person.repository.projection;

import java.time.LocalDate;

/// **Education View Projection**
///
/// View of a person's educational background including high school information for both Czech and foreign institutions.
///
/// @param highSchoolId the high school ID
/// @param highSchoolFieldOfStudyNumber the high school field of study number
/// @param highSchoolCountryId the high school country ID
/// @param graduationDate the high school graduation date
/// @param highSchoolForeign the foreign high school name
/// @param highSchoolForeignPlace the foreign high school location
/// @param highSchoolForeignFieldOfStudy the foreign high school field of study
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record EducationView(
    // High School Information
    String highSchoolId,
    String highSchoolFieldOfStudyNumber,
    Integer highSchoolCountryId,
    LocalDate graduationDate,

    // Foreign High School Information
    String highSchoolForeign,
    String highSchoolForeignPlace,
    String highSchoolForeignFieldOfStudy
) {

}
