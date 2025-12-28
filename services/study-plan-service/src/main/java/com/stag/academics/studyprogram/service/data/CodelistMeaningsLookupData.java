package com.stag.academics.studyprogram.service.data;

import com.stag.academics.shared.grpc.model.CodelistEntryId;
import lombok.Builder;

import java.util.Map;

/// **Codelist Meanings Lookup Data**
///
/// Container for codelist meanings retrieved from the codelist service.
/// Maps entry IDs to their localized meanings for study program enrichment.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Builder
public record CodelistMeaningsLookupData(
    Map<CodelistEntryId, String> codelistMeanings
) {

}
