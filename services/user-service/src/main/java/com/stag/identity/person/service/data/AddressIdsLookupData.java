package com.stag.identity.person.service.data;

import lombok.Builder;

@Builder
public record AddressIdsLookupData(
    Integer birthCountryId,
    Integer countryId,
    Long municipalityId,
    Long municipalityPartId,
    Long districtId
) {

}
