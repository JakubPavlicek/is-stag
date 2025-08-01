package com.stag.identity.user.application.person.dto;

import lombok.Builder;

@Builder
public record PersonEducationData(
    String highSchoolName,
    String highSchoolFieldOfStudy,
    String highSchoolStreet,
    String highSchoolStreetNumber,
    String highSchoolZipCode,
    String highSchoolMunicipalityName,
    String highSchoolDistrictName,
    String highSchoolCountryName
) {

}
