package com.stag.identity.person.service.data;

import lombok.Builder;

/// **Education Lookup Data**
///
/// Enriched education data from codelist service with localized field of study and location names.
/// Contains complete high school details with resolved geographic information.
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
