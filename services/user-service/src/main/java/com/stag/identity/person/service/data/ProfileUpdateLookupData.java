package com.stag.identity.person.service.data;

import lombok.Builder;

/// **Profile Update Lookup Data**
///
/// Validated codelist data for profile update operations.
/// Contains low values (database codes) for marital status and titles, plus resolved birth country ID.
/// Used to ensure only valid codelist values are persisted during updates.
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
