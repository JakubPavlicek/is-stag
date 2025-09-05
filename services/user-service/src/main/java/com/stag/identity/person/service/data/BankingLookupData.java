package com.stag.identity.person.service.data;

import com.stag.identity.shared.grpc.model.CodelistEntryId;
import lombok.Builder;

import java.util.Map;

@Builder
public record BankingLookupData(
    Map<CodelistEntryId, String> codelistMeanings,
    String euroAccountCountryName
) {

}
