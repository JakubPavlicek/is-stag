package com.stag.platform.education.repository.projection;

public record HighSchoolAddressProjection(
    String name,
    String street,
    String zipCode,
    String municipality,
    String district
) {

}
