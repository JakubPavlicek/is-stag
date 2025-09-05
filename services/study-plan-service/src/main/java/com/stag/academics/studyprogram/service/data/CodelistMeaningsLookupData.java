package com.stag.academics.studyprogram.service.data;

import com.stag.academics.shared.grpc.model.CodelistEntryId;
import lombok.Builder;

import java.util.Map;

@Builder
public record CodelistMeaningsLookupData(
    Map<CodelistEntryId, String> codelistMeanings
) {

}
