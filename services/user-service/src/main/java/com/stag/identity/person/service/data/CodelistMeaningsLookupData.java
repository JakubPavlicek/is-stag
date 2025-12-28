package com.stag.identity.person.service.data;

import com.stag.identity.shared.grpc.model.CodelistEntryId;
import lombok.Builder;

import java.util.Map;

/// **Codelist Meanings Lookup Data**
///
/// Generic codelist meanings lookup data for simple profile enrichment.
/// Contains localized codelist values keyed by codelist entry identifiers.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Builder
public record CodelistMeaningsLookupData(
    Map<CodelistEntryId, String> codelistMeanings
) {

}
