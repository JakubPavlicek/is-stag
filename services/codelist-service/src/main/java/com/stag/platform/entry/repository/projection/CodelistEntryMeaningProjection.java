package com.stag.platform.entry.repository.projection;

import com.stag.platform.entry.entity.CodelistEntryId;

/// **Codelist Entry Meaning Projection**
///
/// Projection for codelist entry ID and its language-specific meaning.
///
/// @param id Codelist entry ID
/// @param meaning Codelist entry meaning
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record CodelistEntryMeaningProjection(
    CodelistEntryId id,
    String meaning
) {

}
