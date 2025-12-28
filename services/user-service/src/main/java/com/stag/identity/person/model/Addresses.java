package com.stag.identity.person.model;

import lombok.Builder;

import java.io.Serializable;

/// **Addresses Model**
///
/// Person address information.
/// Includes permanent and temporary addresses with localized country/state data.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Builder
public record Addresses(
    Address permanentAddress,
    Address temporaryAddress,
    ForeignAddress foreignPermanentAddress,
    ForeignAddress foreignTemporaryAddress
) implements Serializable {

    /// Address with full structure including district and municipality part.
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

    /// Foreign address with a simplified structure for international locations.
    @Builder
    public record ForeignAddress(
        String zipCode,
        String municipality,
        String district,
        String postOffice
    ) implements Serializable {

    }

}
