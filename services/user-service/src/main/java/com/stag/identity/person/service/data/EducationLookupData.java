package com.stag.identity.person.service.data;

import lombok.Builder;

@Builder
public record EducationLookupData(
    String highSchoolName,
    String highSchoolFieldOfStudy,
    String highSchoolStreet,
    String highSchoolZipCode,
    String highSchoolMunicipalityName,
    String highSchoolDistrictName,
    String highSchoolCountryName
) {

}
