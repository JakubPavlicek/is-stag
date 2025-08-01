package com.stag.identity.user.application.person.dto;

import lombok.Builder;

@Builder
public record PersonAddressData(
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
