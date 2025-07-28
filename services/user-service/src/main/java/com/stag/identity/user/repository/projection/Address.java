package com.stag.identity.user.repository.projection;

public sealed interface Address permits AddressProjection, ForeignAddressProjection {

    String getAddressType();

}
