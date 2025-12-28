package com.stag.identity.person.service.data;

import com.stag.identity.shared.grpc.model.CodelistEntryId;
import lombok.Builder;

import java.util.Map;

/// **Banking Lookup Data**
///
/// Enriched banking data from codelist service with localized bank names and account types.
/// Contains codelist meanings for bank codes and resolved Euro account country name.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Builder
public record BankingLookupData(
    Map<CodelistEntryId, String> codelistMeanings,
    String euroAccountCountryName
) {

}
