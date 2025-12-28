package com.stag.identity.person.service.data;

import lombok.Builder;

/// **Address Lookup Data**
///
/// Enriched address data from codelist service with localized country and district names.
/// Contains complete address details for both permanent and temporary Czech addresses.
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
