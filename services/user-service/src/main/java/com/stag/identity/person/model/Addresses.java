package com.stag.identity.person.model;

import lombok.Builder;

import java.io.Serializable;

/// **Addresses Model**
///
/// Person address information.
/// Includes permanent and temporary addresses with localized country/state data.
///
/// @param permanentAddress the permanent address
/// @param temporaryAddress the temporary address
/// @param foreignPermanentAddress the foreign permanent address
/// @param foreignTemporaryAddress the foreign temporary address
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
    ///
    /// @param street the street
    /// @param streetNumber the street number
    /// @param zipCode the zip code
    /// @param municipality the municipality
    /// @param municipalityPart the municipality part
    /// @param district the district
    /// @param country the country
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
    ///
    /// @param zipCode the zip code
    /// @param municipality the municipality
    /// @param district the district
    /// @param postOffice the post office
    @Builder
    public record ForeignAddress(
        String zipCode,
        String municipality,
        String district,
        String postOffice
    ) implements Serializable {

    }

}
