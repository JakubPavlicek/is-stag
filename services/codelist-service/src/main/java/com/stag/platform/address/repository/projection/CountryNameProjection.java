package com.stag.platform.address.repository.projection;

/// **Country Name Projection**
///
/// Lightweight projection for country ID and name only.
///
/// @param id Country ID
/// @param name Country name
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record CountryNameProjection(
    Integer id,
    String name
) {

}
