package com.stag.platform.entry.service.dto;

/// **Person Profile Low Values**
///
/// DTO containing low values for person profile codelist entries.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record PersonProfileLowValues(
    String maritalStatusLowValue,
    String titlePrefixLowValue,
    String titleSuffixLowValue
) {
}
