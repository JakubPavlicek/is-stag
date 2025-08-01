package com.stag.identity.user.domain.person.model;

import lombok.Builder;
import lombok.Getter;

/**
 * A Value Object representing a physical address.
 */
@Getter
@Builder
public class Address {
    private final String street;
    private final String streetNumber;
    private final String municipality;
    private final String municipalityPart;
    private final String district;
    private final String zipCode;
    private final String country;
    private final String postOffice; // For foreign addresses
}
