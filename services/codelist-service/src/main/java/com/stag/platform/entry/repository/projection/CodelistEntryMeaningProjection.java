package com.stag.platform.entry.repository.projection;

import com.stag.platform.entry.entity.CodelistEntryId;

public record CodelistEntryMeaningProjection(
    CodelistEntryId id,
    String meaning
) {

}
