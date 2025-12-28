package com.stag.platform.address.exception;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/// **Countries Not Found Exception**
///
/// Thrown when a country lookup fails for one or more IDs.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
public class CountriesNotFoundException extends RuntimeException {

    /// List of country IDs that were not found
    private final List<Integer> missingIds;

    /// Creates a new exception for missing countries.
    ///
    /// @param missingIds List of country IDs that were not found
    public CountriesNotFoundException(List<Integer> missingIds) {
        super("Unable to find countries for IDs: [" + formatMissingIdsMessage(missingIds) + "]");
        this.missingIds = missingIds;
    }

    /// Formats missing IDs into a comma-separated string.
    ///
    /// @param missingIds List of missing country IDs
    /// @return Formatted string of IDs
    private static String formatMissingIdsMessage(List<Integer> missingIds) {
        return missingIds.stream()
                         .map(String::valueOf)
                         .collect(Collectors.joining(", "));
    }

}
