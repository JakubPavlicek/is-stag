package com.stag.platform.codelist.repository.projection;

import com.stag.platform.codelist.entity.CodelistEntryId;

public interface CodelistEntryValue {
    CodelistEntryId getId();
    String getMeaningCz();
    String getMeaningEn();
}
