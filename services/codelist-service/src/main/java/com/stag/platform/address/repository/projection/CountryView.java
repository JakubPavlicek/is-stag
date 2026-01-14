package com.stag.platform.address.repository.projection;

import java.io.Serializable;

/// **Country View Projection**
///
/// Projection for country data with language-specific names.
///
/// @param id Country ID
/// @param name Country name
/// @param commonName Country common name
/// @param abbreviation Country abbreviation
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record CountryView(
    Integer id,
    String name,
    String commonName,
    String abbreviation
) implements Serializable {

}
