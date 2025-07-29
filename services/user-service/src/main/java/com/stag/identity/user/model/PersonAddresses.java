package com.stag.identity.user.model;

import com.stag.identity.user.model.Addresses.ForeignAddress;
import lombok.Builder;

@Builder
public record PersonAddresses(
    PersonAddress permanentAddress,
    PersonAddress temporaryAddress,
    ForeignAddress foreignPermanentAddress,
    ForeignAddress foreignTemporaryAddress
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

}
