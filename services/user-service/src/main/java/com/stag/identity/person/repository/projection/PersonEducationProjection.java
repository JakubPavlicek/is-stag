package com.stag.identity.person.repository.projection;

import java.time.LocalDate;

public record PersonEducationProjection(
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
