package com.stag.identity.user.repository.projection;

public record AddressesProjection(
    AddressProjection permanentResidence,
    AddressProjection temporaryResidence,
    AddressProjection foreignPermanentResidence,
    AddressProjection foreignTemporaryResidence
) {

}
