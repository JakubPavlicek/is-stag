package com.stag.platform.codelist.repository.projection;

public record AddressPlaceNameProjection(
    Long municipalityPartId,
    String municipalityName,
    String municipalityPartName,
    String districtName
) {

}
