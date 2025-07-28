package com.stag.identity.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {
    private String street;
    private String streetNumber;
    private String zipCode;
    private String municipality;
    private String municipalityPart;
    private String district;
    private String country;
}
