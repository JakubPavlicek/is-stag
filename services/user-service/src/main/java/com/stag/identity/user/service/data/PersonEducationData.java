package com.stag.identity.user.service.data;

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
