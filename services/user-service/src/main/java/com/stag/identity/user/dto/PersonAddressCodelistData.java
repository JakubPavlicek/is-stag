package com.stag.identity.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Contains codelist data specifically for person address endpoints
 */
@Data
@Builder
public class PersonAddressCodelistData {
    private Map<CodelistEntryId, String> codelistMeanings;

    // Permanent residence
    private String permanentCountryName;
    private String permanentMunicipalityName;
    private String permanentMunicipalityPartName;
    private String permanentDistrictName;

    // Temporary residence
    private String temporaryCountryName;
    private String temporaryMunicipalityName;
    private String temporaryMunicipalityPartName;
    private String temporaryDistrictName;
}
