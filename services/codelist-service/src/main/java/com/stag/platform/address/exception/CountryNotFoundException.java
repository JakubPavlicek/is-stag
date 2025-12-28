package com.stag.platform.address.exception;

import lombok.Getter;

/// **Country Not Found Exception**
///
/// Thrown when a country lookup by name fails.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
public class CountryNotFoundException extends RuntimeException {

    /// Country name that was not found
    private final String countryName;

    /// Creates a new exception for a missing country.
    ///
    /// @param countryName Country name that was not found
    public CountryNotFoundException(String countryName) {
        super("Country not found: " + countryName);
        this.countryName = countryName;
    }

}
