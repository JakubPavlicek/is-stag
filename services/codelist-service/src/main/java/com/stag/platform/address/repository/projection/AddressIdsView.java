package com.stag.platform.address.repository.projection;

public record AddressIdsView(
    Long municipalityId,
    Long municipalityPartId,
    Integer districtId
) {

}
