package com.stag.identity.shared.grpc.mapper;

import com.stag.identity.person.model.CodelistDomain;
import com.stag.identity.person.model.CodelistEntryId;
import com.stag.identity.person.repository.projection.PersonAddressProjection;
import com.stag.identity.person.repository.projection.PersonBankProjection;
import com.stag.identity.person.repository.projection.PersonEducationProjection;
import com.stag.identity.person.repository.projection.PersonProfileProjection;
import com.stag.identity.person.service.data.PersonAddressData;
import com.stag.identity.person.service.data.PersonBankingData;
import com.stag.identity.person.service.data.PersonEducationData;
import com.stag.identity.person.service.data.PersonProfileData;
import com.stag.platform.codelist.v1.CodelistKey;
import com.stag.platform.codelist.v1.CodelistMeaning;
import com.stag.platform.codelist.v1.GetPersonAddressDataRequest;
import com.stag.platform.codelist.v1.GetPersonAddressDataResponse;
import com.stag.platform.codelist.v1.GetPersonBankingDataRequest;
import com.stag.platform.codelist.v1.GetPersonBankingDataResponse;
import com.stag.platform.codelist.v1.GetPersonEducationDataRequest;
import com.stag.platform.codelist.v1.GetPersonEducationDataResponse;
import com.stag.platform.codelist.v1.GetPersonProfileDataRequest;
import com.stag.platform.codelist.v1.GetPersonProfileDataResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.CollectionMappingStrategy;
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

//@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CodelistMapper {

    CodelistMapper INSTANCE = Mappers.getMapper(CodelistMapper.class);

    @Mapping(source = "language", target = "language")
    @Mapping(source = "personProfile.birthCountryId", target = "birthCountryId")
    @Mapping(source = "personProfile.citizenshipCountryId", target = "citizenshipCountryId")
    @Mapping(target = "codelistKeysList", ignore = true)
    GetPersonProfileDataRequest toPersonProfileDataRequest(PersonProfileProjection personProfile, String language);

    @Mapping(source = "language", target = "language")
    @Mapping(source = "personAddress.permanentCountryId", target = "permanentCountryId")
    @Mapping(source = "personAddress.permanentMunicipalityPartId", target = "permanentMunicipalityPartId")
    @Mapping(source = "personAddress.temporaryCountryId", target = "temporaryCountryId")
    @Mapping(source = "personAddress.temporaryMunicipalityPartId", target = "temporaryMunicipalityPartId")
    GetPersonAddressDataRequest toPersonAddressDataRequest(PersonAddressProjection personAddress, String language);

    @Mapping(source = "language", target = "language")
    @Mapping(source = "personBank.euroAccountCountryId", target = "euroAccountCountryId")
    @Mapping(target = "codelistKeysList", ignore = true)
    GetPersonBankingDataRequest toPersonBankingDataRequest(PersonBankProjection personBank, String language);

    @Mapping(source = "language", target = "language")
    @Mapping(source = "personEducation.highSchoolId", target = "highSchoolId")
    @Mapping(source = "personEducation.highSchoolFieldOfStudyNumber", target = "highSchoolFieldOfStudyNumber")
    @Mapping(source = "personEducation.highSchoolCountryId", target = "highSchoolCountryId")
    GetPersonEducationDataRequest toPersonEducationDataRequest(PersonEducationProjection personEducation, String language);

    @Mapping(source = "codelistMeaningsList", target = "codelistMeanings", qualifiedByName = "toMeaningMap")
    @Mapping(source = "birthCountryName", target = "birthCountryName")
    @Mapping(source = "citizenshipCountryName", target = "citizenshipCountryName")
    PersonProfileData toPersonProfileData(GetPersonProfileDataResponse response);

    @Mapping(source = "personAddress.permanentStreet", target = "permanentStreet")
    @Mapping(source = "personAddress.permanentStreetNumber", target = "permanentStreetNumber")
    @Mapping(source = "personAddress.permanentZipCode", target = "permanentZipCode")
    @Mapping(source = "response.permanentMunicipalityName", target = "permanentMunicipality")
    @Mapping(source = "response.permanentMunicipalityPartName", target = "permanentMunicipalityPart")
    @Mapping(source = "response.permanentDistrictName", target = "permanentDistrict")
    @Mapping(source = "response.permanentCountryName", target = "permanentCountry")
    @Mapping(source = "personAddress.temporaryStreet", target = "temporaryStreet")
    @Mapping(source = "personAddress.temporaryStreetNumber", target = "temporaryStreetNumber")
    @Mapping(source = "personAddress.temporaryZipCode", target = "temporaryZipCode")
    @Mapping(source = "response.temporaryMunicipalityName", target = "temporaryMunicipality")
    @Mapping(source = "response.temporaryMunicipalityPartName", target = "temporaryMunicipalityPart")
    @Mapping(source = "response.temporaryDistrictName", target = "temporaryDistrict")
    @Mapping(source = "response.temporaryCountryName", target = "temporaryCountry")
    PersonAddressData toPersonAddressData(
        GetPersonAddressDataResponse response,
        PersonAddressProjection personAddress
    );

    @Mapping(source = "codelistMeaningsList", target = "codelistMeanings", qualifiedByName = "toMeaningMap")
    @Mapping(source = "euroAccountCountryName", target = "euroAccountCountryName")
    PersonBankingData toPersonBankingData(GetPersonBankingDataResponse response);

    PersonEducationData toPersonEducationData(GetPersonEducationDataResponse response);

    @AfterMapping
    default void addPersonProfileCodelistKeys(
        PersonProfileProjection personProfile,
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
        PersonBankProjection personBank,
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

    private List<CodelistKey> buildProfileCodelistKeys(PersonProfileProjection personProfile) {
        List<CodelistKey> codelistKeys = new ArrayList<>(5);

        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.TITUL_PRED, personProfile.titlePrefix());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.TITUL_ZA, personProfile.titleSuffix());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.POHLAVI, personProfile.gender());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.STAV, personProfile.maritalStatus());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.KVANT_OBCAN, personProfile.citizenshipQualification());

        return codelistKeys;
    }

    private List<CodelistKey> buildBankingCodelistKeys(PersonBankProjection personBank) {
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
