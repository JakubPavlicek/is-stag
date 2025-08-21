package com.stag.identity.person.model;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record PersonAddresses(
    Address permanentAddress,
    Address temporaryAddress,
    ForeignAddress foreignPermanentAddress,
    ForeignAddress foreignTemporaryAddress
) implements Serializable {

    @Builder
    public record Address(
        String street,
        String streetNumber,
        String zipCode,
        String municipality,
        String municipalityPart,
        String district,
        String country
    ) implements Serializable {

    }

    @Builder
    public record ForeignAddress(
        String zipCode,
        String municipality,
        String district,
        String postOffice
    ) implements Serializable {

    }

}
