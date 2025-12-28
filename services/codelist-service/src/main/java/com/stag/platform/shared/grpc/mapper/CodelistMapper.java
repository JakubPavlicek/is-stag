package com.stag.platform.shared.grpc.mapper;

import com.google.protobuf.Message;
import com.stag.platform.address.repository.projection.AddressPlaceNameProjection;
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
import com.stag.platform.codelist.v1.GetPersonProfileUpdateDataResponse;
import com.stag.platform.education.repository.projection.HighSchoolAddressProjection;
import com.stag.platform.entry.entity.CodelistEntryId;
import com.stag.platform.entry.repository.projection.CodelistEntryMeaningProjection;
import com.stag.platform.entry.service.dto.PersonProfileLowValues;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;
import org.springframework.data.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/// **Codelist Mapper**
///
/// MapStruct mapper for converting between gRPC protobuf messages and internal domain models.
/// Handles complex mappings for person profile, address, banking, and education data.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CodelistMapper {

    /// Mapper instance
    CodelistMapper INSTANCE = Mappers.getMapper(CodelistMapper.class);

    /// Extracts country IDs from gRPC request messages.
    ///
    /// @param message gRPC request message
    /// @return Set of country IDs present in the message
    default Set<Integer> extractCountryIds(Message message) {
        return switch (message) {
            case GetPersonProfileDataRequest r -> extractValues(
                Pair.of(r.hasBirthCountryId(), r.getBirthCountryId()),
                Pair.of(r.hasCitizenshipCountryId(), r.getCitizenshipCountryId())
            );
            case GetPersonAddressDataRequest r -> extractValues(
                Pair.of(r.hasPermanentCountryId(), r.getPermanentCountryId()),
                Pair.of(r.hasTemporaryCountryId(), r.getTemporaryCountryId())
            );
            case GetPersonBankingDataRequest r -> extractValues(
                Pair.of(r.hasEuroAccountCountryId(), r.getEuroAccountCountryId())
            );
            case GetPersonEducationDataRequest r -> extractValues(
                Pair.of(r.hasHighSchoolCountryId(), r.getHighSchoolCountryId())
            );
            default -> throw new IllegalStateException("Unexpected value: " + message);
        };
    }

    /// Extracts municipality part IDs from gRPC request messages.
    ///
    /// @param message gRPC request message
    /// @return Set of municipality part IDs present in the message
    default Set<Long> extractMunicipalityPartIds(Message message) {
        return switch (message) {
            case GetPersonAddressDataRequest r -> extractValues(
                Pair.of(r.hasPermanentMunicipalityPartId(), r.getPermanentMunicipalityPartId()),
                Pair.of(r.hasTemporaryMunicipalityPartId(), r.getTemporaryMunicipalityPartId())
            );
            default -> throw new IllegalStateException("Unexpected value: " + message);
        };
    }

    /// Converts gRPC codelist keys to internal codelist entry IDs.
    ///
    /// @param codelistKeys List of gRPC codelist keys
    /// @return List of codelist entry IDs
    List<CodelistEntryId> toCodelistEntryIds(List<CodelistKey> codelistKeys);

    /// Converts internal codelist entries to gRPC codelist meanings.
    ///
    /// @param entries List of codelist entry meaning projections
    /// @return List of gRPC codelist meanings
    List<CodelistMeaning> toCodelistMeanings(List<CodelistEntryMeaningProjection> entries);

    /// Builds person profile data response from request and context data.
    ///
    /// @param request Person profile data request
    /// @param codelistMeanings Codelist meanings context
    /// @param countryNames Country names context
    /// @return Person profile data response
    @Mapping(target = "birthCountryName", source = "birthCountryId", qualifiedByName = "getCountryName")
    @Mapping(target = "citizenshipCountryName", source = "citizenshipCountryId", qualifiedByName = "getCountryName")
    @Mapping(target = "codelistMeanings", ignore = true)
    GetPersonProfileDataResponse buildPersonProfileDataResponse(
        GetPersonProfileDataRequest request,
        @Context List<CodelistMeaning> codelistMeanings,
        @Context Map<Integer, String> countryNames
    );

    /// Builds person profile update data response from low values and country ID.
    ///
    /// @param profileLowValues Person profile low values
    /// @param birthCountryId Birth country ID
    /// @return Person profile update data response
    @Mapping(target = "maritalStatusLowValue", source = "profileLowValues.maritalStatusLowValue")
    @Mapping(target = "titlePrefixLowValue", source = "profileLowValues.titlePrefixLowValue")
    @Mapping(target = "titleSuffixLowValue", source = "profileLowValues.titleSuffixLowValue")
    GetPersonProfileUpdateDataResponse buildPersonProfileUpdateDataResponse(
        PersonProfileLowValues profileLowValues,
        Integer birthCountryId
    );

    /// Builds person address data response from request and context data.
    ///
    /// @param request Person address data request
    /// @param addressNames Address names context
    /// @param countryNames Country names context
    /// @return Person address data response
    @Mapping(target = "permanentCountryName", source = "permanentCountryId", qualifiedByName = "getCountryName")
    @Mapping(target = "temporaryCountryName", source = "temporaryCountryId", qualifiedByName = "getCountryName")
    @Mapping(target = "permanentMunicipalityName", source = "permanentMunicipalityPartId", qualifiedByName = "getMunicipalityName")
    @Mapping(target = "permanentMunicipalityPartName", source = "permanentMunicipalityPartId", qualifiedByName = "getMunicipalityPartName")
    @Mapping(target = "permanentDistrictName", source = "permanentMunicipalityPartId", qualifiedByName = "getDistrictName")
    @Mapping(target = "temporaryMunicipalityName", source = "temporaryMunicipalityPartId", qualifiedByName = "getMunicipalityName")
    @Mapping(target = "temporaryMunicipalityPartName", source = "temporaryMunicipalityPartId", qualifiedByName = "getMunicipalityPartName")
    @Mapping(target = "temporaryDistrictName", source = "temporaryMunicipalityPartId", qualifiedByName = "getDistrictName")
    GetPersonAddressDataResponse buildPersonAddressDataResponse(
        GetPersonAddressDataRequest request,
        @Context Map<Long, AddressPlaceNameProjection> addressNames,
        @Context Map<Integer, String> countryNames
    );

    /// Builds person banking data response from request and context data.
    ///
    /// @param request Person banking data request
    /// @param codelistMeanings Codelist meanings context
    /// @param countryNames Country names context
    /// @return Person banking data response
    @Mapping(target = "euroAccountCountryName", source = "euroAccountCountryId", qualifiedByName = "getCountryName")
    @Mapping(target = "codelistMeanings", ignore = true)
    GetPersonBankingDataResponse buildPersonBankingDataResponse(
        GetPersonBankingDataRequest request,
        @Context List<CodelistMeaning> codelistMeanings,
        @Context Map<Integer, String> countryNames
    );

    /// Builds person education data response from request and context data.
    ///
    /// @param request Person education data request
    /// @param highSchoolAddress High school address projection
    /// @param fieldOfStudy Field of study name
    /// @param countryNames Country names context
    /// @return Person education data response
    @Mapping(target = "highSchoolName", source = "highSchoolAddress.name")
    @Mapping(target = "highSchoolStreet", source = "highSchoolAddress.street")
    @Mapping(target = "highSchoolZipCode", source = "highSchoolAddress.zipCode")
    @Mapping(target = "highSchoolMunicipalityName", source = "highSchoolAddress.municipality")
    @Mapping(target = "highSchoolDistrictName", source = "highSchoolAddress.district")
    @Mapping(target = "highSchoolFieldOfStudy", source = "fieldOfStudy")
    @Mapping(target = "highSchoolCountryName", source = "request.highSchoolCountryId", qualifiedByName = "getCountryName")
    GetPersonEducationDataResponse buildPersonEducationDataResponse(
        GetPersonEducationDataRequest request,
        HighSchoolAddressProjection highSchoolAddress,
        String fieldOfStudy,
        @Context Map<Integer, String> countryNames
    );

    /// Converts codelist entry meaning projection to gRPC codelist meaning.
    ///
    /// @param entry Codelist entry meaning projection
    /// @return gRPC codelist meaning
    @Mapping(target = "domain", source = "id.domain")
    @Mapping(target = "lowValue", source = "id.lowValue")
    @Mapping(target = "meaning", source = "meaning")
    CodelistMeaning toCodelistMeaning(CodelistEntryMeaningProjection entry);

    /// Adds codelist meanings to response builder after mapping.
    ///
    /// @param codelistMeanings List of codelist meanings
    /// @param builder Response builder
    @AfterMapping
    default void addCodelistMeanings(
        @Context List<CodelistMeaning> codelistMeanings,
        @MappingTarget Message.Builder builder
    ) {
        if (codelistMeanings == null) {
            return;
        }

        switch (builder) {
            case GetPersonProfileDataResponse.Builder b -> b.addAllCodelistMeanings(codelistMeanings);
            case GetPersonBankingDataResponse.Builder b -> b.addAllCodelistMeanings(codelistMeanings);
            default -> throw new IllegalStateException("Unexpected value: " + builder);
        }
    }

    /// Retrieves country name from context map.
    ///
    /// @param countryId Country ID
    /// @param countryNames Country names map
    /// @return Country name or null
    @Named("getCountryName")
    default String getCountryName(Integer countryId, @Context Map<Integer, String> countryNames) {
        return Optional.ofNullable(countryNames)
                       .map(names -> names.get(countryId))
                       .orElse(null);
    }

    /// Retrieves municipality name from context map.
    ///
    /// @param municipalityPartId Municipality part ID
    /// @param addressNames Address names map
    /// @return Municipality name or null
    @Named("getMunicipalityName")
    default String getMunicipalityName(
        Long municipalityPartId,
        @Context Map<Long, AddressPlaceNameProjection> addressNames
    ) {
        return Optional.ofNullable(addressNames)
                       .map(names -> names.get(municipalityPartId))
                       .map(AddressPlaceNameProjection::municipalityName)
                       .orElse(null);
    }

    /// Retrieves municipality part name from context map.
    ///
    /// @param municipalityPartId Municipality part ID
    /// @param addressNames Address names map
    /// @return Municipality part name or null
    @Named("getMunicipalityPartName")
    default String getMunicipalityPartName(
        Long municipalityPartId,
        @Context Map<Long, AddressPlaceNameProjection> addressNames
    ) {
        return Optional.ofNullable(addressNames)
                       .map(names -> names.get(municipalityPartId))
                       .map(AddressPlaceNameProjection::municipalityPartName)
                       .orElse(null);
    }

    /// Retrieves district name from context map.
    ///
    /// @param municipalityPartId Municipality part ID
    /// @param addressNames Address names map
    /// @return District name or null
    @Named("getDistrictName")
    default String getDistrictName(
        Long municipalityPartId,
        @Context Map<Long, AddressPlaceNameProjection> addressNames
    ) {
        return Optional.ofNullable(addressNames)
                       .map(names -> names.get(municipalityPartId))
                       .map(AddressPlaceNameProjection::districtName)
                       .orElse(null);
    }

    /// Extracts non-null values from pairs when the first element is true.
    ///
    /// @param pairs Varargs of boolean-value pairs
    /// @return Set of extracted values
    @SafeVarargs
    private <T> Set<T> extractValues(Pair<Boolean, T>... pairs) {
        return Arrays.stream(pairs)
                     .filter(Pair::getFirst)
                     .map(Pair::getSecond)
                     .collect(Collectors.toSet());
    }

}
