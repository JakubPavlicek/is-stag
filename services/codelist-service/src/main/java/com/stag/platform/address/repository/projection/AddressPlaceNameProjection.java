package com.stag.platform.address.repository.projection;

public record AddressPlaceNameProjection(
    Long municipalityPartId,
    String municipalityName,
    String municipalityPartName,
    String districtName
) {

}
