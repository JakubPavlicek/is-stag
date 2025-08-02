package com.stag.identity.user.infrastructure.adapter.out.persistence.person.projection;

public record PersonAddressProjection(
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