package com.stag.identity.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Contains codelist data specifically for person profile endpoints
 */
@Data
@Builder
public class PersonProfileCodelistData {
    private Map<CodelistEntryId, String> codelistMeanings;
    private String birthCountryName;
    private String citizenshipCountryName;
}
