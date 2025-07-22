package com.stag.identity.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Data transfer object for holding all codelist-related data for a person
 * including codelist entries, country names, district names, and municipality names.
 */
@Builder
@Data
public class PersonCodelistData {
    private Map<CodelistEntryId, String> codelistMeanings;
    private String countryName;
    private String municipalityName;
    private String districtName;
    private String birthCountryName;
    private String birthMunicipalityName;
    private String birthDistrictName;
    private String domicileCountryName;
    private String domicileMunicipalityName;
    private String domicileDistrictName;
}
