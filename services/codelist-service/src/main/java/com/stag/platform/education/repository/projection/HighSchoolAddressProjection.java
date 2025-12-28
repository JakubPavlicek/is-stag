package com.stag.platform.education.repository.projection;

/// **High School Address Projection**
///
/// Projection for high school address information.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record HighSchoolAddressProjection(
    String name,
    String street,
    String zipCode,
    String municipality,
    String district
) {

}
