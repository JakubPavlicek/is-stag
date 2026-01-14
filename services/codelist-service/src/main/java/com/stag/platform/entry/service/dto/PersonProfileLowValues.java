package com.stag.platform.entry.service.dto;

/// **Person Profile Low Values**
///
/// DTO containing low values for person profile codelist entries.
///
/// @param maritalStatusLowValue Marital status low value
/// @param titlePrefixLowValue Title prefix low value
/// @param titleSuffixLowValue Title suffix low value
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record PersonProfileLowValues(
    String maritalStatusLowValue,
    String titlePrefixLowValue,
    String titleSuffixLowValue
) {
}
