package com.stag.platform.address.repository.projection;

/// **Address Place Name Projection**
///
/// Projection for municipality part address details including district and municipality names.
///
/// @param municipalityPartId Municipality part ID
/// @param municipalityName Municipality name
/// @param municipalityPartName Municipality part name
/// @param districtName District name
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record AddressPlaceNameProjection(
    Long municipalityPartId,
    String municipalityName,
    String municipalityPartName,
    String districtName
) {

}
