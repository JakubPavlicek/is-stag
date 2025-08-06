package com.stag.identity.person.service.data;

import com.stag.identity.person.model.CodelistEntryId;
import lombok.Builder;

import java.util.Map;

@Builder
public record PersonProfileData(
    Map<CodelistEntryId, String> codelistMeanings,
    String birthCountryName,
    String citizenshipCountryName
) {

}
