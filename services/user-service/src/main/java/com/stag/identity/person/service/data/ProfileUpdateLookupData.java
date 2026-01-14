package com.stag.identity.person.service.data;

import lombok.Builder;

/// **Profile Update Lookup Data**
///
/// Validated codelist data for profile update operations.
/// Contains low values (database codes) for marital status and titles, plus resolved birth country ID.
/// Used to ensure only valid codelist values are persisted during updates.
///
/// @param maritalStatusLowValue the marital status low value to validate
/// @param titlePrefixLowValue the title prefix low value to validate
/// @param titleSuffixLowValue the title suffix low value to validate
/// @param birthCountryId the resolved birth country ID
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Builder
public record ProfileUpdateLookupData(
    String maritalStatusLowValue,
    String titlePrefixLowValue,
    String titleSuffixLowValue,
    Integer birthCountryId
) {

}
