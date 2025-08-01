package com.stag.identity.user.application.person.dto;

import com.stag.identity.user.domain.shared.model.CodelistEntryId;
import lombok.Builder;

import java.util.Map;

@Builder
public record PersonProfileData(
    Map<CodelistEntryId, String> codelistMeanings,
    String birthCountryName,
    String citizenshipCountryName
) {

}
