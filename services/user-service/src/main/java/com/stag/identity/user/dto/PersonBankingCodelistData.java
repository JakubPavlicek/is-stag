package com.stag.identity.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Contains codelist data specifically for person banking endpoints
 */
@Data
@Builder
public class PersonBankingCodelistData {
    private Map<CodelistEntryId, String> codelistMeanings;
}
