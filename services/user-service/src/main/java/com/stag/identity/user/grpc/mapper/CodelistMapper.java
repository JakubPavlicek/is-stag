package com.stag.identity.user.grpc.mapper;

import com.stag.identity.user.model.AddressType;
import com.stag.identity.user.model.CodelistDomain;
import com.stag.identity.user.model.CodelistEntryId;
import com.stag.identity.user.repository.projection.AddressProjection;
import com.stag.identity.user.repository.projection.PersonProfileProjection;
import com.stag.identity.user.service.data.PersonAddressData;
import com.stag.identity.user.service.data.PersonProfileData;
import com.stag.platform.codelist.v1.CodelistKey;
import com.stag.platform.codelist.v1.CodelistValue;
import com.stag.platform.codelist.v1.GetPersonAddressDataRequest;
import com.stag.platform.codelist.v1.GetPersonAddressDataResponse;
import com.stag.platform.codelist.v1.GetPersonProfileDataRequest;
import com.stag.platform.codelist.v1.GetPersonProfileDataResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class CodelistMapper {

    private static final String LANGUAGE = "cs";

    public GetPersonProfileDataRequest toPersonProfileDataRequest(PersonProfileProjection personProfile) {
        var requestBuilder = GetPersonProfileDataRequest.newBuilder()
                                                        .setLanguage(LANGUAGE);

        // Build codelist keys for profile fields
        List<CodelistKey> codelistKeys = buildProfileCodelistKeys(personProfile);
        requestBuilder.addAllCodelistKeys(codelistKeys);

        // Add country IDs if present
        setIfPresent(personProfile.birthCountryId(), requestBuilder::setBirthCountryId);
        setIfPresent(personProfile.residenceCountryId(), requestBuilder::setCitizenshipCountryId);

        return requestBuilder.build();
    }

    public PersonProfileData toPersonProfileData(GetPersonProfileDataResponse response) {
        List<CodelistValue> codelistValues = response.getCodelistValuesList();
        Map<CodelistEntryId, String> codelistMeanings = buildCodelistMeanings(codelistValues);

        return PersonProfileData.builder()
                                .codelistMeanings(codelistMeanings)
                                .birthCountryName(response.getBirthCountryName())
                                .citizenshipCountryName(response.getCitizenshipCountryName())
                                .build();
    }

    public GetPersonAddressDataRequest toPersonAddressDataRequest(
        AddressProjection permanentAddress,
        AddressProjection temporaryAddress
    ) {
        var requestBuilder = GetPersonAddressDataRequest.newBuilder()
                                                        .setLanguage(LANGUAGE);

        setAddressIfPresent(permanentAddress, requestBuilder, AddressType.PERMANENT);
        setAddressIfPresent(temporaryAddress, requestBuilder, AddressType.TEMPORARY);

        return requestBuilder.build();
    }

    public PersonAddressData toPersonAddressData(
        GetPersonAddressDataResponse response,
        AddressProjection permanentAddress,
        AddressProjection temporaryAddress
    ) {
        return PersonAddressData.builder()
                                .permanentStreet(permanentAddress.street())
                                .permanentStreetNumber(permanentAddress.streetNumber())
                                .permanentZipCode(permanentAddress.zipCode())
                                .permanentMunicipality(response.getPermanentMunicipalityName())
                                .permanentMunicipalityPart(response.getPermanentMunicipalityPartName())
                                .permanentDistrict(response.getPermanentDistrictName())
                                .permanentCountry(response.getPermanentCountryName())
                                .temporaryStreet(temporaryAddress.street())
                                .temporaryStreetNumber(temporaryAddress.streetNumber())
                                .temporaryZipCode(temporaryAddress.zipCode())
                                .temporaryMunicipality(response.getTemporaryMunicipalityName())
                                .temporaryMunicipalityPart(response.getTemporaryMunicipalityPartName())
                                .temporaryDistrict(response.getTemporaryDistrictName())
                                .temporaryCountry(response.getTemporaryCountryName())
                                .build();
    }

    private List<CodelistKey> buildProfileCodelistKeys(PersonProfileProjection personProfile) {
        List<CodelistKey> codelistKeys = new ArrayList<>();

        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.TITUL_PRED, personProfile.titlePrefix());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.TITUL_ZA, personProfile.titleSuffix());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.POHLAVI, personProfile.gender());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.STAV, personProfile.maritalStatus());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.KVANT_OBCAN, personProfile.citizenshipQualification());

        return codelistKeys;
    }

    private void addCodelistKeyIfPresent(List<CodelistKey> codelistKeys, CodelistDomain domain, String lowValue) {
        if (lowValue != null) {
            codelistKeys.add(createCodelistKey(domain, lowValue));
        }
    }

    private CodelistKey createCodelistKey(CodelistDomain domain, String lowValue) {
        return CodelistKey.newBuilder()
                          .setDomain(domain.name())
                          .setLowValue(lowValue)
                          .build();
    }

    private Map<CodelistEntryId, String> buildCodelistMeanings(List<CodelistValue> codelistValues) {
        return codelistValues.stream()
                             .collect(Collectors.toMap(
                                 this::buildCodelistEntryId,
                                 CodelistValue::getMeaning
                             ));
    }

    private CodelistEntryId buildCodelistEntryId(CodelistValue codelistValue) {
        return new CodelistEntryId(codelistValue.getDomain(), codelistValue.getLowValue());
    }

    private void setAddressIfPresent(
        AddressProjection address,
        GetPersonAddressDataRequest.Builder requestBuilder,
        AddressType addressType
    ) {
        if (address == null) {
            return;
        }

        if (addressType == AddressType.PERMANENT) {
            setIfPresent(address.municipalityId(), requestBuilder::setPermanentMunicipalityId);
            setIfPresent(address.municipalityPartId(), requestBuilder::setPermanentMunicipalityPartId);
            setIfPresent(address.districtId(), requestBuilder::setPermanentDistrictId);
            setIfPresent(address.countryId(), requestBuilder::setPermanentCountryId);
        }
        else if (addressType == AddressType.TEMPORARY) {
            setIfPresent(address.municipalityId(), requestBuilder::setTemporaryMunicipalityId);
            setIfPresent(address.municipalityPartId(), requestBuilder::setTemporaryMunicipalityPartId);
            setIfPresent(address.districtId(), requestBuilder::setTemporaryDistrictId);
            setIfPresent(address.countryId(), requestBuilder::setTemporaryCountryId);
        }
    }

    private <T> void setIfPresent(T value, Consumer<T> setter) {
        Optional.ofNullable(value).ifPresent(setter);
    }

}
