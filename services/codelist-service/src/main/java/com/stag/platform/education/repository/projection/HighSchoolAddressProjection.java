package com.stag.platform.education.repository.projection;

/// **High School Address Projection**
///
/// Projection for high school address information.
///
/// @param name High school name
/// @param street Street address
/// @param zipCode Zip code
/// @param municipality Municipality name
/// @param district District name
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
