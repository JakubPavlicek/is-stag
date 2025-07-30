package com.stag.identity.user.grpc.mapper;

import com.stag.identity.user.model.AddressType;
import com.stag.identity.user.model.CodelistDomain;
import com.stag.identity.user.model.CodelistEntryId;
import com.stag.identity.user.repository.projection.PersonAddressProjection;
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

    public GetPersonAddressDataRequest toPersonAddressDataRequest(PersonAddressProjection personAddress) {
        var requestBuilder = GetPersonAddressDataRequest.newBuilder()
                                                        .setLanguage(LANGUAGE);

        setAddressIfPresent(personAddress, requestBuilder, AddressType.PERMANENT);
        setAddressIfPresent(personAddress, requestBuilder, AddressType.TEMPORARY);

        return requestBuilder.build();
    }

    public PersonAddressData toPersonAddressData(
        GetPersonAddressDataResponse response,
        PersonAddressProjection personAddress
    ) {
        return PersonAddressData.builder()
                                .permanentStreet(personAddress.permanentStreet())
                                .permanentStreetNumber(personAddress.permanentStreetNumber())
                                .permanentZipCode(personAddress.permanentZipCode())
                                .permanentMunicipality(response.getPermanentMunicipalityName())
                                .permanentMunicipalityPart(response.getPermanentMunicipalityPartName())
                                .permanentDistrict(response.getPermanentDistrictName())
                                .permanentCountry(response.getPermanentCountryName())
                                .temporaryStreet(personAddress.temporaryStreet())
                                .temporaryStreetNumber(personAddress.temporaryStreetNumber())
                                .temporaryZipCode(personAddress.temporaryZipCode())
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
        PersonAddressProjection personAddress,
        GetPersonAddressDataRequest.Builder requestBuilder,
        AddressType addressType
    ) {
        if (addressType == AddressType.PERMANENT) {
            setIfPresent(personAddress.permanentMunicipalityPartId(), requestBuilder::setPermanentMunicipalityPartId);
            setIfPresent(personAddress.permanentCountryId(), requestBuilder::setPermanentCountryId);
        }
        else if (addressType == AddressType.TEMPORARY) {
            setIfPresent(personAddress.temporaryMunicipalityPartId(), requestBuilder::setTemporaryMunicipalityPartId);
            setIfPresent(personAddress.temporaryCountryId(), requestBuilder::setTemporaryCountryId);
        }
    }

    private <T> void setIfPresent(T value, Consumer<T> setter) {
        Optional.ofNullable(value).ifPresent(setter);
    }

}
