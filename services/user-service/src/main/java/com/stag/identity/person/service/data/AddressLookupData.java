package com.stag.identity.person.service.data;

import lombok.Builder;

/// **Address Lookup Data**
///
/// Enriched address data from codelist service with localized country and district names.
/// Contains complete address details for both permanent and temporary Czech addresses.
///
/// @param permanentStreet the permanent street address
/// @param permanentStreetNumber the permanent street number
/// @param permanentZipCode the permanent ZIP code
/// @param permanentMunicipality the permanent municipality name
/// @param permanentMunicipalityPart the permanent municipality part name
/// @param permanentDistrict the permanent district name
/// @param permanentCountry the permanent country name
/// @param temporaryStreet the temporary street address
/// @param temporaryStreetNumber the temporary street number
/// @param temporaryZipCode the temporary ZIP code
/// @param temporaryMunicipality the temporary municipality name
/// @param temporaryMunicipalityPart the temporary municipality part name
/// @param temporaryDistrict the temporary district name
/// @param temporaryCountry the temporary country name
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Builder
public record AddressLookupData(
    // Permanent address
    String permanentStreet,
    String permanentStreetNumber,
    String permanentZipCode,
    String permanentMunicipality,
    String permanentMunicipalityPart,
    String permanentDistrict,
    String permanentCountry,

    // Temporary address
    String temporaryStreet,
    String temporaryStreetNumber,
    String temporaryZipCode,
    String temporaryMunicipality,
    String temporaryMunicipalityPart,
    String temporaryDistrict,
    String temporaryCountry
) {

}
