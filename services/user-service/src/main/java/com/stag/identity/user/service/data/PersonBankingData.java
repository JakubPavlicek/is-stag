package com.stag.identity.user.service.data;

import com.stag.identity.user.model.CodelistEntryId;
import lombok.Builder;

import java.util.Map;

@Builder
public record PersonBankingData(
    Map<CodelistEntryId, String> codelistMeanings,
    String euroAccountCountryName
) {

}
