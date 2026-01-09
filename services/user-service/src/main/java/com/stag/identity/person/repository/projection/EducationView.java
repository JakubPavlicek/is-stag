package com.stag.identity.person.repository.projection;

import java.time.LocalDate;

/// **Education View Projection**
///
/// View of a person's educational background including high school information for both Czech and foreign institutions.
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
