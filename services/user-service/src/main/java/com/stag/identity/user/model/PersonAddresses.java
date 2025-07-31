package com.stag.identity.user.model;

import lombok.Builder;

@Builder
public record PersonAddresses(
    Address permanentAddress,
    Address temporaryAddress,
    ForeignAddress foreignPermanentAddress,
    ForeignAddress foreignTemporaryAddress
) {

    @Builder
    public record Address(
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
    public record ForeignAddress(
        String zipCode,
        String municipality,
        String district,
        String postOffice
    ) {

    }

    @Builder
    public record HighSchoolAddress(
        String street,
        String streetNumber,
        String zipCode,
        String municipality,
        String district,
        String country
    ) {

    }

}
