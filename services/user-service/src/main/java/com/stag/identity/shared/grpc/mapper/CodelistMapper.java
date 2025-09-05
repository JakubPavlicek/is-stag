package com.stag.identity.shared.grpc.mapper;

import com.stag.identity.shared.grpc.model.CodelistDomain;
import com.stag.identity.shared.grpc.model.CodelistEntryId;
import com.stag.identity.person.repository.projection.AddressView;
import com.stag.identity.person.repository.projection.BankView;
import com.stag.identity.person.repository.projection.EducationView;
import com.stag.identity.person.repository.projection.ProfileView;
import com.stag.identity.person.repository.projection.SimpleProfileView;
import com.stag.identity.person.service.data.AddressLookupData;
import com.stag.identity.person.service.data.BankingLookupData;
import com.stag.identity.person.service.data.CodelistMeaningsLookupData;
import com.stag.identity.person.service.data.EducationLookupData;
import com.stag.identity.person.service.data.ProfileLookupData;
import com.stag.platform.codelist.v1.CodelistKey;
import com.stag.platform.codelist.v1.CodelistMeaning;
import com.stag.platform.codelist.v1.GetCodelistValuesRequest;
import com.stag.platform.codelist.v1.GetCodelistValuesResponse;
import com.stag.platform.codelist.v1.GetPersonAddressDataRequest;
import com.stag.platform.codelist.v1.GetPersonAddressDataResponse;
import com.stag.platform.codelist.v1.GetPersonBankingDataRequest;
import com.stag.platform.codelist.v1.GetPersonBankingDataResponse;
import com.stag.platform.codelist.v1.GetPersonEducationDataRequest;
import com.stag.platform.codelist.v1.GetPersonEducationDataResponse;
import com.stag.platform.codelist.v1.GetPersonProfileDataRequest;
import com.stag.platform.codelist.v1.GetPersonProfileDataResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CodelistMapper {

    CodelistMapper INSTANCE = Mappers.getMapper(CodelistMapper.class);

    @Mapping(target = "codelistKeys", ignore = true)
    GetCodelistValuesRequest toCodelistValuesRequest(SimpleProfileView simpleProfile, String language);

    @Mapping(target = "birthCountryId", source = "personProfile.birthCountryId")
    @Mapping(target = "citizenshipCountryId", source = "personProfile.citizenshipCountryId")
    @Mapping(target = "codelistKeys", ignore = true)
    GetPersonProfileDataRequest toPersonProfileDataRequest(ProfileView personProfile, String language);

    @Mapping(target = "permanentCountryId", source = "personAddress.permanentCountryId")
    @Mapping(target = "permanentMunicipalityPartId", source = "personAddress.permanentMunicipalityPartId")
    @Mapping(target = "temporaryCountryId", source = "personAddress.temporaryCountryId")
    @Mapping(target = "temporaryMunicipalityPartId", source = "personAddress.temporaryMunicipalityPartId")
    GetPersonAddressDataRequest toPersonAddressDataRequest(AddressView personAddress, String language);

    @Mapping(target = "euroAccountCountryId", source = "personBank.euroAccountCountryId")
    @Mapping(target = "codelistKeys", ignore = true)
    GetPersonBankingDataRequest toPersonBankingDataRequest(BankView personBank, String language);

    @Mapping(target = "highSchoolId", source = "personEducation.highSchoolId")
    @Mapping(target = "highSchoolFieldOfStudyNumber", source = "personEducation.highSchoolFieldOfStudyNumber")
    @Mapping(target = "highSchoolCountryId", source = "personEducation.highSchoolCountryId")
    GetPersonEducationDataRequest toPersonEducationDataRequest(EducationView personEducation, String language);

    @Mapping(target = "codelistMeanings", source = "codelistMeanings", qualifiedByName = "toMeaningMap")
    CodelistMeaningsLookupData toCodelistMeaningsData(GetCodelistValuesResponse response);

    @Mapping(target = "codelistMeanings", source = "codelistMeanings", qualifiedByName = "toMeaningMap")
    ProfileLookupData toPersonProfileData(GetPersonProfileDataResponse response);

    @Mapping(target = "permanentStreet", source = "personAddress.permanentStreet")
    @Mapping(target = "permanentStreetNumber", source = "personAddress.permanentStreetNumber")
    @Mapping(target = "permanentZipCode", source = "personAddress.permanentZipCode")
    @Mapping(target = "permanentMunicipality", source = "response.permanentMunicipalityName")
    @Mapping(target = "permanentMunicipalityPart", source = "response.permanentMunicipalityPartName")
    @Mapping(target = "permanentDistrict", source = "response.permanentDistrictName")
    @Mapping(target = "permanentCountry", source = "response.permanentCountryName")
    @Mapping(target = "temporaryStreet", source = "personAddress.temporaryStreet")
    @Mapping(target = "temporaryStreetNumber", source = "personAddress.temporaryStreetNumber")
    @Mapping(target = "temporaryZipCode", source = "personAddress.temporaryZipCode")
    @Mapping(target = "temporaryMunicipality", source = "response.temporaryMunicipalityName")
    @Mapping(target = "temporaryMunicipalityPart", source = "response.temporaryMunicipalityPartName")
    @Mapping(target = "temporaryDistrict", source = "response.temporaryDistrictName")
    @Mapping(target = "temporaryCountry", source = "response.temporaryCountryName")
    AddressLookupData toPersonAddressData(
        GetPersonAddressDataResponse response,
        AddressView personAddress
    );

    @Mapping(target = "codelistMeanings", source = "codelistMeanings", qualifiedByName = "toMeaningMap")
    BankingLookupData toPersonBankingData(GetPersonBankingDataResponse response);

    EducationLookupData toPersonEducationData(GetPersonEducationDataResponse response);

    @AfterMapping
    default void addSimpleProfileCodelistKeys(
        SimpleProfileView simpleProfile,
        @MappingTarget GetCodelistValuesRequest.Builder builder
    ) {
        if (simpleProfile == null) {
            return;
        }

        List<CodelistKey> keys = buildSimpleProfileCodelistKeys(simpleProfile);
        builder.addAllCodelistKeys(keys);
    }

    @AfterMapping
    default void addPersonProfileCodelistKeys(
        ProfileView personProfile,
        @MappingTarget GetPersonProfileDataRequest.Builder builder
    ) {
        if (personProfile == null) {
            return;
        }

        List<CodelistKey> keys = buildProfileCodelistKeys(personProfile);
        builder.addAllCodelistKeys(keys);
    }

    @AfterMapping
    default void addPersonBankingCodelistKeys(
        BankView personBank,
        @MappingTarget GetPersonBankingDataRequest.Builder builder
    ) {
        if (personBank == null) {
            return;
        }

        List<CodelistKey> keys = buildBankingCodelistKeys(personBank);
        builder.addAllCodelistKeys(keys);
    }

    @Named("toMeaningMap")
    default Map<CodelistEntryId, String> toMeaningMap(List<CodelistMeaning> codelistMeanings) {
        return codelistMeanings.stream()
                               .collect(Collectors.toMap(
                                   cm -> new CodelistEntryId(cm.getDomain(), cm.getLowValue()),
                                   CodelistMeaning::getMeaning
                               ));
    }

    private List<CodelistKey> buildSimpleProfileCodelistKeys(SimpleProfileView simpleProfile) {
        List<CodelistKey> codelistKeys = new ArrayList<>(3);

        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.TITUL_PRED, simpleProfile.titlePrefix());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.TITUL_ZA, simpleProfile.titleSuffix());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.POHLAVI, simpleProfile.gender());

        return codelistKeys;
    }

    private List<CodelistKey> buildProfileCodelistKeys(ProfileView personProfile) {
        List<CodelistKey> codelistKeys = new ArrayList<>(5);

        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.TITUL_PRED, personProfile.titlePrefix());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.TITUL_ZA, personProfile.titleSuffix());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.POHLAVI, personProfile.gender());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.STAV, personProfile.maritalStatus());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.KVANT_OBCAN, personProfile.citizenshipQualification());

        return codelistKeys;
    }

    private List<CodelistKey> buildBankingCodelistKeys(BankView personBank) {
        List<CodelistKey> codelistKeys = new ArrayList<>(2);

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

}
