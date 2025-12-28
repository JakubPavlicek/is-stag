package com.stag.identity.person.repository.projection;

/// **Address View Projection**
///
/// Comprehensive view of person addresses including permanent (domicile),
/// temporary, and foreign addresses. Supports both Czech and international
/// address formats.
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