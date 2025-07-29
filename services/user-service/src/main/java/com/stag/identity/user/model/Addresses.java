package com.stag.identity.user.model;

import lombok.Builder;

@Builder
public record Addresses(
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
        Long municipalityId,
        Long municipalityPartId,
        Integer districtId,
        Integer countryId
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

}
