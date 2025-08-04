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
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

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

        // Set address place names
        if (!addressNames.isEmpty()) {
            setAddressPlaceNames(request, addressNames, responseBuilder);
        }

        // Set country names
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
            responseBuilder.setHighSchoolName(highSchoolAddress.name())
                           .setHighSchoolStreet(highSchoolAddress.street())
                           .setHighSchoolZipCode(highSchoolAddress.zipCode())
                           .setHighSchoolMunicipalityName(highSchoolAddress.municipality())
                           .setHighSchoolDistrictName(highSchoolAddress.district());
        }

        responseBuilder.setHighSchoolFieldOfStudy(fieldOfStudy);

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
                responseBuilder.setPermanentMunicipalityName(permanentAddress.municipalityName())
                               .setPermanentMunicipalityPartName(permanentAddress.municipalityPartName())
                               .setPermanentDistrictName(permanentAddress.districtName());
            }
        }

        // Set temporary address place names
        if (request.hasTemporaryMunicipalityPartId()) {
            AddressPlaceNameProjection temporaryAddress = addressNames.get(request.getTemporaryMunicipalityPartId());
            if (temporaryAddress != null) {
                responseBuilder.setTemporaryMunicipalityName(temporaryAddress.municipalityName())
                               .setTemporaryMunicipalityPartName(temporaryAddress.municipalityPartName())
                               .setTemporaryDistrictName(temporaryAddress.districtName());
            }
        }
    }

    private void setCountryNameIfPresent(
        Map<Integer, String> countryNames,
        Integer countryId,
        Consumer<String> setter
    ) {
        if (countryId != null) {
            String countryName = countryNames.get(countryId);
            if (countryName != null) {
                setter.accept(countryName);
            }
        }
    }

    @SafeVarargs
    private <T> Set<T> extractValues(Pair<Boolean, T>... pairs) {
        Set<T> result = HashSet.newHashSet(pairs.length);

        for (Pair<Boolean, T> pair : pairs) {
            boolean hasValue = pair.getFirst();
            T value = pair.getSecond();

            if (hasValue) {
                result.add(value);
            }
        }

        return result;
    }

    private CodelistValue toCodelistValue(CodelistEntryValue entry) {
        return CodelistValue.newBuilder()
                            .setDomain(entry.id().getDomain())
                            .setLowValue(entry.id().getLowValue())
                            .setMeaning(entry.meaning())
                            .build();
    }

}
