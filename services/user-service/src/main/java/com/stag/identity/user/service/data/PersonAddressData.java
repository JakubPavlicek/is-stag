package com.stag.identity.user.service.data;

import com.stag.identity.user.model.Address;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class PersonAddressData {
    // Permanent residence
    private String permanentStreet;
    private String permanentStreetNumber;
    private String permanentZipCode;
    private String permanentMunicipality;
    private String permanentMunicipalityPart;
    private String permanentDistrict;
    private String permanentCountry;

    // Temporary residence
    private String temporaryStreet;
    private String temporaryStreetNumber;
    private String temporaryZipCode;
    private String temporaryMunicipality;
    private String temporaryMunicipalityPart;
    private String temporaryDistrict;
    private String temporaryCountry;
}
