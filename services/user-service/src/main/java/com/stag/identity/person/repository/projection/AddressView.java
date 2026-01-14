package com.stag.identity.person.repository.projection;

/// **Address View Projection**
///
/// Comprehensive view of person addresses including permanent (domicile), temporary, and foreign addresses.
/// Supports both Czech and international address formats.
///
/// @param permanentStreet the permanent street address
/// @param permanentStreetNumber the permanent street number
/// @param permanentZipCode the permanent ZIP code
/// @param permanentMunicipalityPartId the permanent municipality part ID
/// @param permanentCountryId the permanent country ID
/// @param temporaryStreet the temporary street address
/// @param temporaryStreetNumber the temporary street number
/// @param temporaryZipCode the temporary ZIP code
/// @param temporaryMunicipalityPartId the temporary municipality part ID
/// @param temporaryCountryId the temporary country ID
/// @param foreignPermanentZipCode the foreign permanent ZIP code
/// @param foreignPermanentMunicipality the foreign permanent municipality
/// @param foreignPermanentDistrict the foreign permanent district
/// @param foreignPermanentPostOffice the foreign permanent post office
/// @param foreignTemporaryZipCode the foreign temporary ZIP code
/// @param foreignTemporaryMunicipality the foreign temporary municipality
/// @param foreignTemporaryDistrict the foreign temporary district
/// @param foreignTemporaryPostOffice the foreign temporary post office
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record AddressView(
    String permanentStreet,
    String permanentStreetNumber,
    String permanentZipCode,
    Long permanentMunicipalityPartId,
    Integer permanentCountryId,

    String temporaryStreet,
    String temporaryStreetNumber,
    String temporaryZipCode,
    Long temporaryMunicipalityPartId,
    Integer temporaryCountryId,

    String foreignPermanentZipCode,
    String foreignPermanentMunicipality,
    String foreignPermanentDistrict,
    String foreignPermanentPostOffice,

    String foreignTemporaryZipCode,
    String foreignTemporaryMunicipality,
    String foreignTemporaryDistrict,
    String foreignTemporaryPostOffice
) {

}