package com.stag.identity.user.repository.projection;

public record AddressProjection(
    String street,
    String streetNumber,
    String zipCode,
    String municipality,
    String municipalityPart,
    String district,
    String state
) {

}