package com.stag.identity.user.repository.projection;

public record PersonAddressProjection(
    String permanentStreet,
    String permanentStreetNumber,
    String permanentZipCode,
    Long permanentMunicipalityId,
    Long permanentMunicipalityPartId,
    Integer permanentDistrictId,
    Integer permanentCountryId,

    String temporaryStreet,
    String temporaryStreetNumber,
    String temporaryZipCode,
    Long temporaryMunicipalityId,
    Long temporaryMunicipalityPartId,
    Integer temporaryDistrictId,
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