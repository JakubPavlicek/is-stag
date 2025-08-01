package com.stag.identity.user.infrastructure.person.adapter.out.persistence.projection;

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
