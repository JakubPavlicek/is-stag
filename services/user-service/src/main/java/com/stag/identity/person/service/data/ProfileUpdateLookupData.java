package com.stag.identity.person.service.data;

import lombok.Builder;

@Builder
public record ProfileUpdateLookupData(
    String maritalStatusLowValue,
    String titlePrefixLowValue,
    String titleSuffixLowValue,
    Integer birthCountryId
) {

}
