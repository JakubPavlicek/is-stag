package com.stag.identity.user.service.data;

import com.stag.identity.user.model.CodelistEntryId;
import lombok.Builder;

import java.util.Map;

@Builder
public record PersonProfileData(
    Map<CodelistEntryId, String> codelistMeanings,
    String birthCountryName,
    String citizenshipCountryName
) {

}
