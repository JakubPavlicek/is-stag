package com.stag.platform.codelist.repository.projection;

import com.stag.platform.codelist.entity.CodelistEntryId;

public record CodelistEntryValue(
    CodelistEntryId id,
    String meaning
) {

}
