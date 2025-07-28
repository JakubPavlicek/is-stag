package com.stag.identity.user.repository.projection;

public record ForeignAddressProjection(
    String addressType,
    String zipCode,
    String municipality,
    String district,
    String postOffice
) implements Address {

    @Override
    public String getAddressType() {
        return addressType;
    }

}
