package com.stag.platform.address.exception;

import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/// **Municipality Parts Not Found Exception**
///
/// Thrown when the municipality part lookup fails for one or more IDs.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Getter
public class MunicipalityPartsNotFoundException extends RuntimeException {

    /// List of municipality part IDs that were not found
    private final List<Long> missingIds;

    /// Creates a new exception for missing municipality parts.
    ///
    /// @param missingIds List of municipality part IDs that were not found
    public MunicipalityPartsNotFoundException(List<Long> missingIds) {
        super("Unable to find municipality parts for IDs: [" + formatMissingIdsMessage(missingIds) + "]");
        this.missingIds = missingIds;
    }

    /// Formats missing IDs into a comma-separated string.
    ///
    /// @param missingIds List of missing municipality part IDs
    /// @return Formatted string of IDs
    private static String formatMissingIdsMessage(List<Long> missingIds) {
        return missingIds.stream()
                         .map(String::valueOf)
                         .collect(Collectors.joining(", "));
    }

}