package com.stag.identity.person.service.data;

import com.stag.identity.shared.grpc.model.CodelistEntryId;
import lombok.Builder;

import java.util.Map;

/// **Profile Lookup Data**
///
/// Enriched data from codelist service for profile mappings.
/// Contains localized codelist meanings (nationality, marital status, titles, gender) and resolved country names for birthplace and citizenship.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Builder
public record ProfileLookupData(
    Map<CodelistEntryId, String> codelistMeanings,
    String birthCountryName,
    String citizenshipCountryName
) {

}
