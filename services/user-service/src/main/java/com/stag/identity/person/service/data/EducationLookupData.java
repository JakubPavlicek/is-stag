package com.stag.identity.person.service.data;

import lombok.Builder;

/// **Education Lookup Data**
///
/// Enriched education data from codelist service with localized field of study and location names.
/// Contains complete high school details with resolved geographic information.
///
/// @param highSchoolName the high school name
/// @param highSchoolFieldOfStudy the high school field of study
/// @param highSchoolStreet the high school street address
/// @param highSchoolZipCode the high school zip code
/// @param highSchoolMunicipalityName the high school municipality name
/// @param highSchoolDistrictName the high school district name
/// @param highSchoolCountryName the high school country name
///
/// @author Jakub Pavlíček
/// @version 1.0.0
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
