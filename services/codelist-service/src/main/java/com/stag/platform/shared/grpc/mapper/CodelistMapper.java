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
import com.stag.platform.education.repository.projection.HighSchoolAddressProjection;
import com.stag.platform.entry.entity.CodelistEntryId;
import com.stag.platform.entry.repository.projection.CodelistEntryMeaningProjection;
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

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CodelistMapper {

    CodelistMapper INSTANCE = Mappers.getMapper(CodelistMapper.class);

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

    default Set<Long> extractMunicipalityPartIds(Message message) {
        return switch (message) {
            case GetPersonAddressDataRequest r -> extractValues(
                Pair.of(r.hasPermanentMunicipalityPartId(), r.getPermanentMunicipalityPartId()),
                Pair.of(r.hasTemporaryMunicipalityPartId(), r.getTemporaryMunicipalityPartId())
            );
            default -> throw new IllegalStateException("Unexpected value: " + message);
        };
    }

    List<CodelistEntryId> toCodelistEntryIds(List<CodelistKey> codelistKeys);

    List<CodelistMeaning> toCodelistMeanings(List<CodelistEntryMeaningProjection> entries);

    @Mapping(target = "birthCountryName", source = "birthCountryId", qualifiedByName = "getCountryName")
    @Mapping(target = "citizenshipCountryName", source = "citizenshipCountryId", qualifiedByName = "getCountryName")
    @Mapping(target = "codelistMeaningsList", ignore = true)
    @Mapping(target = "allFields", ignore = true)
    @Mapping(target = "unknownFields", ignore = true)
    GetPersonProfileDataResponse buildPersonProfileDataResponse(
        GetPersonProfileDataRequest request,
        @Context List<CodelistMeaning> codelistMeanings,
        @Context Map<Integer, String> countryNames
    );

    @Mapping(target = "permanentCountryName", source = "permanentCountryId", qualifiedByName = "getCountryName")
    @Mapping(target = "temporaryCountryName", source = "temporaryCountryId", qualifiedByName = "getCountryName")
    @Mapping(target = "permanentMunicipalityName", source = "permanentMunicipalityPartId", qualifiedByName = "getMunicipalityName")
    @Mapping(target = "permanentMunicipalityPartName", source = "permanentMunicipalityPartId", qualifiedByName = "getMunicipalityPartName")
    @Mapping(target = "permanentDistrictName", source = "permanentMunicipalityPartId", qualifiedByName = "getDistrictName")
    @Mapping(target = "temporaryMunicipalityName", source = "temporaryMunicipalityPartId", qualifiedByName = "getMunicipalityName")
    @Mapping(target = "temporaryMunicipalityPartName", source = "temporaryMunicipalityPartId", qualifiedByName = "getMunicipalityPartName")
    @Mapping(target = "temporaryDistrictName", source = "temporaryMunicipalityPartId", qualifiedByName = "getDistrictName")
    @Mapping(target = "allFields", ignore = true)
    @Mapping(target = "unknownFields", ignore = true)
    GetPersonAddressDataResponse buildPersonAddressDataResponse(
        GetPersonAddressDataRequest request,
        @Context Map<Long, AddressPlaceNameProjection> addressNames,
        @Context Map<Integer, String> countryNames
    );

    @Mapping(target = "euroAccountCountryName", source = "euroAccountCountryId", qualifiedByName = "getCountryName")
    @Mapping(target = "codelistMeaningsList", ignore = true)
    @Mapping(target = "allFields", ignore = true)
    @Mapping(target = "unknownFields", ignore = true)
    GetPersonBankingDataResponse buildPersonBankingDataResponse(
        GetPersonBankingDataRequest request,
        @Context List<CodelistMeaning> codelistMeanings,
        @Context Map<Integer, String> countryNames
    );

    @Mapping(target = "highSchoolName", source = "highSchoolAddress.name")
    @Mapping(target = "highSchoolStreet", source = "highSchoolAddress.street")
    @Mapping(target = "highSchoolZipCode", source = "highSchoolAddress.zipCode")
    @Mapping(target = "highSchoolMunicipalityName", source = "highSchoolAddress.municipality")
    @Mapping(target = "highSchoolDistrictName", source = "highSchoolAddress.district")
    @Mapping(target = "highSchoolFieldOfStudy", source = "fieldOfStudy")
    @Mapping(target = "highSchoolCountryName", source = "request.highSchoolCountryId", qualifiedByName = "getCountryName")
    @Mapping(target = "allFields", ignore = true)
    @Mapping(target = "unknownFields", ignore = true)
    GetPersonEducationDataResponse buildPersonEducationDataResponse(
        GetPersonEducationDataRequest request,
        HighSchoolAddressProjection highSchoolAddress,
        String fieldOfStudy,
        @Context Map<Integer, String> countryNames
    );

    @Mapping(target = "domain", source = "id.domain")
    @Mapping(target = "lowValue", source = "id.lowValue")
    @Mapping(target = "meaning", source = "meaning")
    CodelistMeaning toCodelistMeaning(CodelistEntryMeaningProjection entry);

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

    @Named("getCountryName")
    default String getCountryName(Integer countryId, @Context Map<Integer, String> countryNames) {
        return Optional.ofNullable(countryNames)
                       .map(names -> names.get(countryId))
                       .orElse(null);
    }

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

    @SafeVarargs
    private <T> Set<T> extractValues(Pair<Boolean, T>... pairs) {
        return Arrays.stream(pairs)
                     .filter(Pair::getFirst)
                     .map(Pair::getSecond)
                     .collect(Collectors.toSet());
    }

}
