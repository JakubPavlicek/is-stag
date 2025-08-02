package com.stag.identity.user.infrastructure.adapter.out.persistence.person.projection;

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
