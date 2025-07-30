package com.stag.identity.user.model;

import lombok.Builder;

@Builder
public record PersonAddresses(
    PersonAddress permanentAddress,
    PersonAddress temporaryAddress,
    PersonForeignAddress foreignPermanentAddress,
    PersonForeignAddress foreignTemporaryAddress
) {

    @Builder
    public record PersonAddress(
        String street,
        String streetNumber,
        String zipCode,
        String municipality,
        String municipalityPart,
        String district,
        String country
    ) {

    }

    @Builder
    public record PersonForeignAddress(
        String zipCode,
        String municipality,
        String district,
        String postOffice
    ) {

    }

}
