package com.stag.platform.codelist.grpc.mapper;

import com.google.protobuf.Message;
import com.stag.platform.codelist.entity.CodelistEntryId;
import com.stag.platform.codelist.repository.projection.AddressPlaceNameProjection;
import com.stag.platform.codelist.repository.projection.CodelistEntryValue;
import com.stag.platform.codelist.repository.projection.HighSchoolAddressProjection;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CodelistMapper {

    public List<CodelistEntryId> extractCodelistEntryIds(List<CodelistKey> codelistKeys) {
        return codelistKeys.stream()
                           .map(key -> new CodelistEntryId(key.getDomain(), key.getLowValue()))
                           .toList();
    }

    public List<CodelistValue> mapToCodelistValues(List<CodelistEntryValue> entries) {
        return entries.stream()
                      .map(this::toCodelistValue)
                      .toList();
    }

    public Set<Integer> extractCountryIds(Message message) {
        log.info("extractCountryIds thread: {}", Thread.currentThread());

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

    public Set<Long> extractMunicipalityPartIds(Message message) {
        log.info("extractMunicipalityPartIds thread: {}", Thread.currentThread());

        return switch (message) {
            case GetPersonAddressDataRequest r -> extractValues(
                Pair.of(r.hasPermanentMunicipalityPartId(), r.getPermanentMunicipalityPartId()),
                Pair.of(r.hasTemporaryMunicipalityPartId(), r.getTemporaryMunicipalityPartId())
            );
            default -> throw new IllegalStateException("Unexpected value: " + message);
        };
    }

    public GetPersonProfileDataResponse buildPersonProfileDataResponse(
        GetPersonProfileDataRequest request,
        List<CodelistValue> codelistValues,
        Map<Integer, String> countryNames
    ) {
        var responseBuilder = GetPersonProfileDataResponse.newBuilder()
                                                          .addAllCodelistValues(codelistValues);

        if (!countryNames.isEmpty()) {
            setCountryNameIfPresent(countryNames, request.getBirthCountryId(), responseBuilder::setBirthCountryName);
            setCountryNameIfPresent(countryNames, request.getCitizenshipCountryId(), responseBuilder::setCitizenshipCountryName);
        }

        return responseBuilder.build();
    }

    public GetPersonAddressDataResponse buildPersonAddressDataResponse(
        GetPersonAddressDataRequest request,
        Map<Long, AddressPlaceNameProjection> addressNames,
        Map<Integer, String> countryNames
    ) {
        var responseBuilder = GetPersonAddressDataResponse.newBuilder();

        if (!addressNames.isEmpty()) {
            setAddressPlaceNames(request, addressNames, responseBuilder);
        }

        if (!countryNames.isEmpty()) {
            setCountryNameIfPresent(countryNames, request.getPermanentCountryId(), responseBuilder::setPermanentCountryName);
            setCountryNameIfPresent(countryNames, request.getTemporaryCountryId(), responseBuilder::setTemporaryCountryName);
        }

        return responseBuilder.build();
    }

    public GetPersonBankingDataResponse buildPersonBankingDataResponse(
        GetPersonBankingDataRequest request,
        List<CodelistValue> codelistValues,
        Map<Integer, String> countryNames
    ) {
        var responseBuilder = GetPersonBankingDataResponse.newBuilder()
                                                          .addAllCodelistValues(codelistValues);

        if (!countryNames.isEmpty()) {
            setCountryNameIfPresent(countryNames, request.getEuroAccountCountryId(), responseBuilder::setEuroAccountCountryName);
        }

        return responseBuilder.build();
    }

    public GetPersonEducationDataResponse buildPersonEducationDataResponse(
        GetPersonEducationDataRequest request,
        HighSchoolAddressProjection highSchoolAddress,
        String fieldOfStudy,
        Map<Integer, String> countryNames
    ) {
        var responseBuilder = GetPersonEducationDataResponse.newBuilder();

        if (highSchoolAddress != null) {
            setIfPresent(highSchoolAddress.name(), responseBuilder::setHighSchoolName);
            setIfPresent(highSchoolAddress.street(), responseBuilder::setHighSchoolStreet);
            setIfPresent(highSchoolAddress.zipCode(), responseBuilder::setHighSchoolZipCode);
            setIfPresent(highSchoolAddress.municipality(), responseBuilder::setHighSchoolMunicipalityName);
            setIfPresent(highSchoolAddress.district(), responseBuilder::setHighSchoolDistrictName);
        }

        setIfPresent(fieldOfStudy, responseBuilder::setHighSchoolFieldOfStudy);

        if (!countryNames.isEmpty()) {
            setCountryNameIfPresent(countryNames, request.getHighSchoolCountryId(), responseBuilder::setHighSchoolCountryName);
        }

        return responseBuilder.build();
    }

    private void setAddressPlaceNames(
        GetPersonAddressDataRequest request,
        Map<Long, AddressPlaceNameProjection> addressNames,
        GetPersonAddressDataResponse.Builder responseBuilder
    ) {
        // Set permanent address place names
        if (request.hasPermanentMunicipalityPartId()) {
            AddressPlaceNameProjection permanentAddress = addressNames.get(request.getPermanentMunicipalityPartId());
            if (permanentAddress != null) {
                setIfPresent(permanentAddress.municipalityName(), responseBuilder::setPermanentMunicipalityName);
                setIfPresent(permanentAddress.municipalityPartName(), responseBuilder::setPermanentMunicipalityPartName);
                setIfPresent(permanentAddress.districtName(), responseBuilder::setPermanentDistrictName);
            }
        }

        // Set temporary address place names
        if (request.hasTemporaryMunicipalityPartId()) {
            AddressPlaceNameProjection temporaryAddress = addressNames.get(request.getTemporaryMunicipalityPartId());
            if (temporaryAddress != null) {
                setIfPresent(temporaryAddress.municipalityName(), responseBuilder::setTemporaryMunicipalityName);
                setIfPresent(temporaryAddress.municipalityPartName(), responseBuilder::setTemporaryMunicipalityPartName);
                setIfPresent(temporaryAddress.districtName(), responseBuilder::setTemporaryDistrictName);
            }
        }
    }

    private void setCountryNameIfPresent(
        Map<Integer, String> countryNames,
        Integer countryId,
        Consumer<String> setter
    ) {
        Optional.ofNullable(countryId)
                .map(countryNames::get)
                .ifPresent(setter);
    }

    private <T> void setIfPresent(T value, Consumer<T> setter) {
        Optional.ofNullable(value).ifPresent(setter);
    }

    @SafeVarargs
    private <T> Set<T> extractValues(Pair<Boolean, T>... pairs) {
        return Arrays.stream(pairs)
                     .filter(Pair::getFirst)
                     .map(Pair::getSecond)
                     .collect(Collectors.toSet());
    }

    private CodelistValue toCodelistValue(CodelistEntryValue entry) {
        return CodelistValue.newBuilder()
                            .setDomain(entry.id().getDomain())
                            .setLowValue(entry.id().getLowValue())
                            .setMeaning(entry.meaning())
                            .build();
    }

}
