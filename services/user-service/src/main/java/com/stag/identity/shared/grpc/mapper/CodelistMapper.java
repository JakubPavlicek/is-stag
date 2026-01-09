package com.stag.identity.shared.grpc.mapper;

import com.stag.identity.person.model.Profile;
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
import com.stag.identity.person.service.data.ProfileUpdateLookupData;
import com.stag.identity.shared.grpc.model.CodelistDomain;
import com.stag.identity.shared.grpc.model.CodelistEntryId;
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
import com.stag.platform.codelist.v1.GetPersonProfileUpdateDataRequest;
import com.stag.platform.codelist.v1.GetPersonProfileUpdateDataResponse;
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

/// **Codelist gRPC Mapper**
///
/// MapStruct mapper for codelist-service gRPC communication.
/// Transforms projections to gRPC request messages and response messages to lookup data.
/// Handles codelist key building for batch lookups of localized values.
/// Uses @AfterMapping hooks to dynamically construct codelist keys based on available data.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CodelistMapper {

    /// CodelistMapper Instance
    CodelistMapper INSTANCE = Mappers.getMapper(CodelistMapper.class);

    /// Maps simple profile view to codelist values request (codelist keys added in @AfterMapping).
    ///
    /// @param simpleProfile the simple profile projection
    /// @param language the language code for localized values
    /// @return gRPC request for codelist values
    @Mapping(target = "codelistKeys", ignore = true)
    GetCodelistValuesRequest toCodelistValuesRequest(SimpleProfileView simpleProfile, String language);

    /// Maps profile view to person profile data request (codelist keys added in @AfterMapping).
    ///
    /// @param personProfile the profile projection
    /// @param language the language code for localized values
    /// @return gRPC request for person profile data
    @Mapping(target = "birthCountryId", source = "personProfile.birthCountryId")
    @Mapping(target = "citizenshipCountryId", source = "personProfile.citizenshipCountryId")
    @Mapping(target = "codelistKeys", ignore = true)
    GetPersonProfileDataRequest toPersonProfileDataRequest(ProfileView personProfile, String language);

    /// Maps profile update data to gRPC request.
    ///
    /// @param maritalStatus the marital status code
    /// @param birthCountryName the birth country name
    /// @param titles the person's titles
    /// @return gRPC request for profile update data
    @Mapping(target = "titlePrefix", source = "titles.prefix")
    @Mapping(target = "titleSuffix", source = "titles.suffix")
    GetPersonProfileUpdateDataRequest toPersonProfileUpdateDataRequest(String maritalStatus, String birthCountryName, Profile.Titles titles);

    /// Maps address view to person address data request.
    ///
    /// @param personAddress the address projection
    /// @param language the language code for localized values
    /// @return gRPC request for person address data
    @Mapping(target = "permanentCountryId", source = "personAddress.permanentCountryId")
    @Mapping(target = "permanentMunicipalityPartId", source = "personAddress.permanentMunicipalityPartId")
    @Mapping(target = "temporaryCountryId", source = "personAddress.temporaryCountryId")
    @Mapping(target = "temporaryMunicipalityPartId", source = "personAddress.temporaryMunicipalityPartId")
    GetPersonAddressDataRequest toPersonAddressDataRequest(AddressView personAddress, String language);

    /// Maps bank view to person banking data request (codelist keys added in @AfterMapping).
    ///
    /// @param personBank the bank projection
    /// @param language the language code for localized values
    /// @return gRPC request for person banking data
    @Mapping(target = "euroAccountCountryId", source = "personBank.euroAccountCountryId")
    @Mapping(target = "codelistKeys", ignore = true)
    GetPersonBankingDataRequest toPersonBankingDataRequest(BankView personBank, String language);

    /// Maps education view to person education data request.
    ///
    /// @param personEducation the education projection
    /// @param language the language code for localized values
    /// @return gRPC request for person education data
    @Mapping(target = "highSchoolId", source = "personEducation.highSchoolId")
    @Mapping(target = "highSchoolFieldOfStudyNumber", source = "personEducation.highSchoolFieldOfStudyNumber")
    @Mapping(target = "highSchoolCountryId", source = "personEducation.highSchoolCountryId")
    GetPersonEducationDataRequest toPersonEducationDataRequest(EducationView personEducation, String language);

    /// Maps codelist values response to lookup data.
    ///
    /// @param response the gRPC response
    /// @return codelist meanings lookup data
    @Mapping(target = "codelistMeanings", source = "codelistMeanings", qualifiedByName = "toMeaningMap")
    CodelistMeaningsLookupData toCodelistMeaningsData(GetCodelistValuesResponse response);

    /// Maps person profile data response to lookup data.
    ///
    /// @param response the gRPC response
    /// @return profile lookup data
    @Mapping(target = "codelistMeanings", source = "codelistMeanings", qualifiedByName = "toMeaningMap")
    ProfileLookupData toPersonProfileData(GetPersonProfileDataResponse response);

    /// Maps profile update data response to lookup data.
    ///
    /// @param response the gRPC response
    /// @return profile update lookup data
    ProfileUpdateLookupData toProfileUpdateLookupData(GetPersonProfileUpdateDataResponse response);

    /// Maps person address data response to lookup data.
    ///
    /// @param response the gRPC response
    /// @param personAddress the address projection
    /// @return address lookup data
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

    /// Maps person banking data response to lookup data.
    ///
    /// @param response the gRPC response
    /// @return banking lookup data
    @Mapping(target = "codelistMeanings", source = "codelistMeanings", qualifiedByName = "toMeaningMap")
    BankingLookupData toPersonBankingData(GetPersonBankingDataResponse response);

    /// Maps person education data response to lookup data.
    ///
    /// @param response the gRPC response
    /// @return education lookup data
    EducationLookupData toPersonEducationData(GetPersonEducationDataResponse response);

    /// Post-mapping hook to add codelist keys for simple profile.
    ///
    /// @param simpleProfile the simple profile projection
    /// @param builder the request builder to populate
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

    /// Post-mapping hook to add codelist keys for person profile.
    ///
    /// @param personProfile the profile projection
    /// @param builder the request builder to populate
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

    /// Post-mapping hook to add codelist keys for person banking.
    ///
    /// @param personBank the bank projection
    /// @param builder the request builder to populate
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

    /// Transforms a list of codelist meanings into a map keyed by codelist entry ID.
    ///
    /// @param codelistMeanings the list of codelist meanings
    /// @return map of codelist entry IDs to their localized meanings
    @Named("toMeaningMap")
    default Map<CodelistEntryId, String> toMeaningMap(List<CodelistMeaning> codelistMeanings) {
        return codelistMeanings.stream()
                               .collect(Collectors.toMap(
                                   cm -> new CodelistEntryId(cm.getDomain(), cm.getLowValue()),
                                   CodelistMeaning::getMeaning
                               ));
    }

    /// Builds codelist keys for simple profile lookup.
    ///
    /// @param simpleProfile the simple profile projection
    /// @return list of codelist keys for title prefix, suffix, and gender
    private List<CodelistKey> buildSimpleProfileCodelistKeys(SimpleProfileView simpleProfile) {
        List<CodelistKey> codelistKeys = new ArrayList<>(3);

        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.TITUL_PRED, simpleProfile.titlePrefix());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.TITUL_ZA, simpleProfile.titleSuffix());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.POHLAVI, simpleProfile.gender());

        return codelistKeys;
    }

    /// Builds codelist keys for full profile lookup.
    ///
    /// @param personProfile the profile projection
    /// @return list of codelist keys for titles, gender, marital status, and citizenship qualification
    private List<CodelistKey> buildProfileCodelistKeys(ProfileView personProfile) {
        List<CodelistKey> codelistKeys = new ArrayList<>(5);

        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.TITUL_PRED, personProfile.titlePrefix());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.TITUL_ZA, personProfile.titleSuffix());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.POHLAVI, personProfile.gender());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.STAV, personProfile.maritalStatus());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.KVANT_OBCAN, personProfile.citizenshipQualification());

        return codelistKeys;
    }

    /// Builds codelist keys for banking data lookup.
    ///
    /// @param personBank the bank projection
    /// @return list of codelist keys for account bank and euro account bank
    private List<CodelistKey> buildBankingCodelistKeys(BankView personBank) {
        List<CodelistKey> codelistKeys = new ArrayList<>(2);

        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.CIS_BANK, personBank.accountBank());
        addCodelistKeyIfPresent(codelistKeys, CodelistDomain.CIS_BANK_EURO, personBank.euroAccountBank());

        return codelistKeys;
    }

    /// Adds a codelist key to the list if the low value is present.
    ///
    /// @param codelistKeys the list to add to
    /// @param domain the codelist domain
    /// @param lowValue the low value (code), may be null
    private void addCodelistKeyIfPresent(List<CodelistKey> codelistKeys, CodelistDomain domain, String lowValue) {
        if (lowValue != null) {
            codelistKeys.add(createCodelistKey(domain, lowValue));
        }
    }

    /// Creates a codelist key protobuf message.
    ///
    /// @param domain the codelist domain
    /// @param lowValue the low value (code)
    /// @return a CodelistKey protobuf message
    private CodelistKey createCodelistKey(CodelistDomain domain, String lowValue) {
        return CodelistKey.newBuilder()
                          .setDomain(domain.name())
                          .setLowValue(lowValue)
                          .build();
    }

}
