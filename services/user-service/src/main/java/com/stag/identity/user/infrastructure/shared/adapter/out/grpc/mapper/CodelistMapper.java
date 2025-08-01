package com.stag.identity.user.infrastructure.shared.adapter.out.grpc.mapper;

import com.stag.identity.user.application.person.dto.PersonAddressData;
import com.stag.identity.user.application.person.dto.PersonBankingData;
import com.stag.identity.user.application.person.dto.PersonEducationData;
import com.stag.identity.user.application.person.dto.PersonProfileData;
import com.stag.identity.user.domain.person.model.AddressType;
import com.stag.identity.user.domain.person.model.PersonAddress;
import com.stag.identity.user.domain.person.model.PersonBank;
import com.stag.identity.user.domain.person.model.PersonEducation;
import com.stag.identity.user.domain.person.model.PersonProfile;
import com.stag.identity.user.domain.shared.model.CodelistDomain;
import com.stag.identity.user.domain.shared.model.CodelistEntryId;
import com.stag.platform.codelist.v1.CodelistKey;
import com.stag.platform.codelist.v1.CodelistValue;
import com.stag.platform.codelist.v1.GetPersonAddressDataRequest;
import com.stag.platform.codelist.v1.GetPersonAddressDataResponse;
import com.stag.platform.codelist.v1.GetPersonBankingDataRequest;
import com.stag.platform.codelist.v1.GetPersonBankingDataResponse;
import com.stag.platform.codelist.v1.GetPersonEducationDataRequest;
import com.stag.platform.codelist.v1.GetPersonEducationDataResponse;
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

    public GetPersonProfileDataRequest toPersonProfileDataRequest(PersonProfile personProfile) {
        var requestBuilder = GetPersonProfileDataRequest.newBuilder()
                                                        .setLanguage(LANGUAGE);

        List<CodelistKey> codelistKeys = buildProfileCodelistKeys(personProfile);
        requestBuilder.addAllCodelistKeys(codelistKeys);

        setIfPresent(personProfile.birthCountryId(), requestBuilder::setBirthCountryId);
        setIfPresent(personProfile.residenceCountryId(), requestBuilder::setCitizenshipCountryId);

        return requestBuilder.build();
    }

    public GetPersonAddressDataRequest toPersonAddressDataRequest(PersonAddress personAddress) {
        var requestBuilder = GetPersonAddressDataRequest.newBuilder()
                                                        .setLanguage(LANGUAGE);

        setAddressIfPresent(personAddress, requestBuilder, AddressType.PERMANENT);
        setAddressIfPresent(personAddress, requestBuilder, AddressType.TEMPORARY);

        return requestBuilder.build();
    }

    public GetPersonBankingDataRequest toPersonBankingDataRequest(PersonBank personBank) {
        var requestBuilder = GetPersonBankingDataRequest.newBuilder()
                                                        .setLanguage(LANGUAGE);

        List<CodelistKey> codelistKeys = buildBankingCodelistKeys(personBank);
        requestBuilder.addAllCodelistKeys(codelistKeys);

        setIfPresent(personBank.euroAccountCountryId(), requestBuilder::setEuroAccountCountryId);

        return requestBuilder.build();
    }

    public GetPersonEducationDataRequest toPersonEducationDataRequest(PersonEducation personEducation) {
        var requestBuilder = GetPersonEducationDataRequest.newBuilder()
                                                          .setLanguage(LANGUAGE);

        setIfPresent(personEducation.highSchoolId(), requestBuilder::setHighSchoolId);
        setIfPresent(personEducation.highSchoolFieldOfStudyNumber(), requestBuilder::setHighSchoolFieldOfStudyNumber);
        setIfPresent(personEducation.highSchoolCountryId(), requestBuilder::setHighSchoolCountryId);

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

    public PersonAddressData toPersonAddressData(
        GetPersonAddressDataResponse response,
        PersonAddress personAddress
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

    public PersonBankingData toPersonBankingData(GetPersonBankingDataResponse response) {
        List<CodelistValue> codelistValues = response.getCodelistValuesList();
        Map<CodelistEntryId, String> codelistMeanings = buildCodelistMeanings(codelistValues);

        return PersonBankingData.builder()
                                .codelistMeanings(codelistMeanings)
                                .euroAccountCountryName(response.getEuroAccountCountryName())
                                .build();
    }

    public PersonEducationData toPersonEducationData(GetPersonEducationDataResponse response) {
        return PersonEducationData.builder()
                                  .highSchoolName(response.getHighSchoolName())
                                  .highSchoolFieldOfStudy(response.getHighSchoolFieldOfStudy())
                                  .highSchoolStreet(response.getHighSchoolStreet())
                                  .highSchoolZipCode(response.getHighSchoolZipCode())
                                  .highSchoolMunicipalityName(response.getHighSchoolMunicipalityName())
                                  .highSchoolDistrictName(response.getHighSchoolDistrictName())
                                  .highSchoolCountryName(response.getHighSchoolCountryName())
                                  .build();
    }

    private List<CodelistKey> buildProfileCodelistKeys(PersonProfile personProfile) {
        List<CodelistKey> codelistKeys = new ArrayList<>();

        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.TITUL_PRED, personProfile.titlePrefix());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.TITUL_ZA, personProfile.titleSuffix());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.POHLAVI, personProfile.gender());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.STAV, personProfile.maritalStatus());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.KVANT_OBCAN, personProfile.citizenshipQualification());

        return codelistKeys;
    }

    private List<CodelistKey> buildBankingCodelistKeys(PersonBank personBank) {
        List<CodelistKey> codelistKeys = new ArrayList<>();

        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.CIS_BANK, personBank.accountBank());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.CIS_BANK_EURO, personBank.euroAccountBank());

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
        PersonAddress personAddress,
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
