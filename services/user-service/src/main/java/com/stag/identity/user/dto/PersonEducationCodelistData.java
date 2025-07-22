package com.stag.identity.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Contains codelist data specifically for person education endpoints
 */
@Data
@Builder
public class PersonEducationCodelistData {
    private Map<CodelistEntryId, String> codelistMeanings;
    private String highSchoolCountryName;
}
