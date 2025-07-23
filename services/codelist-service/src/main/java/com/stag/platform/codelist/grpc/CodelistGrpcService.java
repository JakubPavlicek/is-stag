package com.stag.platform.codelist.grpc;

import com.stag.platform.codelist.entity.CodelistEntryId;
import com.stag.platform.codelist.entity.Country;
import com.stag.platform.codelist.entity.District;
import com.stag.platform.codelist.entity.Municipality;
import com.stag.platform.codelist.entity.MunicipalityPart;
import com.stag.platform.codelist.projection.CodelistEntryValue;
import com.stag.platform.codelist.service.CodelistService;
import com.stag.platform.codelist.service.CountryService;
import com.stag.platform.codelist.service.DistrictService;
import com.stag.platform.codelist.service.MunicipalityPartService;
import com.stag.platform.codelist.service.MunicipalityService;
import com.stag.platform.codelist.v1.CodelistKey;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import com.stag.platform.codelist.v1.CodelistValue;
import com.stag.platform.codelist.v1.CountryName;
import com.stag.platform.codelist.v1.CountryValue;
import com.stag.platform.codelist.v1.DistrictValue;
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
import com.stag.platform.codelist.v1.MunicipalityPartValue;
import com.stag.platform.codelist.v1.MunicipalityValue;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CodelistGrpcService extends CodelistServiceGrpc.CodelistServiceImplBase {

    private static final String CZECH_LANGUAGE = "cs";

    private final CodelistService codelistService;
    private final CountryService countryService;
    private final MunicipalityService municipalityService;
    private final MunicipalityPartService municipalityPartService;
    private final DistrictService districtService;

    @Override
    public void getCodelistValues(GetCodelistValuesRequest request, StreamObserver<GetCodelistValuesResponse> responseObserver) {
        var response = GetCodelistValuesResponse.newBuilder()
                                                .addAllCodelistValues(buildCodelistValues(
                                                    request.getCodelistKeysList(),
                                                    request.getLanguage()
                                                ))
                                                .build();

        completeResponse(responseObserver, response);
    }

    @Override
    public void getPersonProfileData(GetPersonProfileDataRequest request, StreamObserver<GetPersonProfileDataResponse> responseObserver) {
        CompletableFuture<List<CodelistValue>> codelistValuesFuture = CompletableFuture.supplyAsync(
            () -> buildCodelistValues(request.getCodelistKeysList(), request.getLanguage())
        );

        CompletableFuture<Map<Integer, String>> countryNamesFuture = CompletableFuture.supplyAsync(
            () -> getCountryNames(request)
        );

        codelistValuesFuture.thenCombine(countryNamesFuture, (codelistValues, countryNames) ->
                                buildPersonProfileDataResponse(request, codelistValues, countryNames)
                            )
                            .thenAccept(response -> completeResponse(responseObserver, response))
                            .exceptionally(ex -> errorResponse(responseObserver, ex));
    }

    @Override
    public void getPersonAddressData(GetPersonAddressDataRequest request, StreamObserver<GetPersonAddressDataResponse> responseObserver) {
        var responseBuilder = GetPersonAddressDataResponse.newBuilder();

        // Add permanent residence data
        addAddressData(request, responseBuilder, true);

        // Add temporary residence data
        addAddressData(request, responseBuilder, false);

        completeResponse(responseObserver, responseBuilder.build());
    }

    @Override
    public void getPersonBankingData(
        GetPersonBankingDataRequest request,
        StreamObserver<GetPersonBankingDataResponse> responseObserver
    ) {
        var response =
            GetPersonBankingDataResponse.newBuilder()
                                        .addAllCodelistValues(buildCodelistValues(
                                            request.getCodelistKeysList(),
                                            request.getLanguage()
                                        ))
                                        .build();

        completeResponse(responseObserver, response);
    }

    @Override
    public void getPersonEducationData(
        GetPersonEducationDataRequest request,
        StreamObserver<GetPersonEducationDataResponse> responseObserver
    ) {
        var responseBuilder =
            GetPersonEducationDataResponse.newBuilder()
                                          .addAllCodelistValues(buildCodelistValues(
                                              request.getCodelistKeysList(),
                                              request.getLanguage()
                                          ));

        // Add high school country if present
        if (request.hasHighSchoolCountryId()) {
            countryService.findById(request.getHighSchoolCountryId())
                          .map(this::toCountryValue)
                          .ifPresent(responseBuilder::setHighSchoolCountry);
        }

        completeResponse(responseObserver, responseBuilder.build());
    }

    // Private helper methods

    private List<CodelistValue> buildCodelistValues(List<CodelistKey> codelistKeys, String language) {
        if (codelistKeys.isEmpty()) {
            return List.of();
        }

        List<CodelistEntryId> entryIds = codelistKeys.stream()
                                                     .map(this::toCodelistEntryId)
                                                     .toList();

        return codelistService.getCodelistEntryMeanings(entryIds)
                              .stream()
                              .map(entry -> toCodelistValue(entry, language))
                              .toList();
    }

    private Map<Integer, String> getCountryNames(GetPersonProfileDataRequest request) {
        // If neither country is present, skip adding names
        if (!request.hasBirthCountryId() && !request.hasCitizenshipCountryId()) {
            return Collections.emptyMap();
        }

        List<Integer> countryIds = Stream.of(
                                             request.hasBirthCountryId() ? request.getBirthCountryId() : null,
                                             request.hasCitizenshipCountryId() ? request.getCitizenshipCountryId() : null
                                         )
                                         .filter(Objects::nonNull)
                                         .toList();

        return countryService.findNamesByIds(countryIds);
    }

    private GetPersonProfileDataResponse buildPersonProfileDataResponse(
        GetPersonProfileDataRequest request,
        List<CodelistValue> codelistValues,
        Map<Integer, String> countryNames
    ) {
        var responseBuilder = GetPersonProfileDataResponse.newBuilder()
                                                          .addAllCodelistValues(codelistValues);

        // Add country names if present
        if (!countryNames.isEmpty()) {
            if (request.hasBirthCountryId() && countryNames.containsKey(request.getBirthCountryId())) {
                responseBuilder.setBirthCountryName(toCountryName(countryNames.get(request.getBirthCountryId())));
            }
            if (request.hasCitizenshipCountryId() && countryNames.containsKey(request.getCitizenshipCountryId())) {
                responseBuilder.setCitizenshipCountryName(toCountryName(countryNames.get(request.getCitizenshipCountryId())));
            }
        }

        return responseBuilder.build();
    }

    private void addAddressData(
        GetPersonAddressDataRequest request,
        GetPersonAddressDataResponse.Builder responseBuilder,
        boolean isPermanent
    ) {
        // Country
        getCountryId(request, isPermanent)
            .flatMap(countryService::findById)
            .map(this::toCountryValue)
            .ifPresent(country -> setCountry(responseBuilder, country, isPermanent));

        // Municipality
        getMunicipalityId(request, isPermanent)
            .flatMap(municipalityService::findById)
            .map(this::toMunicipalityValue)
            .ifPresent(municipality -> setMunicipality(responseBuilder, municipality, isPermanent));

        // Municipality Part
        getMunicipalityPartId(request, isPermanent)
            .flatMap(municipalityPartService::findById)
            .map(this::toMunicipalityPartValue)
            .ifPresent(part -> setMunicipalityPart(responseBuilder, part, isPermanent));

        // District
        getDistrictId(request, isPermanent)
            .flatMap(districtService::findById)
            .map(this::toDistrictValue)
            .ifPresent(district -> setDistrict(responseBuilder, district, isPermanent));
    }

    private Optional<Integer> getCountryId(GetPersonAddressDataRequest request, boolean isPermanent) {
        return isPermanent && request.hasPermanentCountryId() ?
            Optional.of(request.getPermanentCountryId()) :
            !isPermanent && request.hasTemporaryCountryId() ?
                Optional.of(request.getTemporaryCountryId()) : Optional.empty();
    }

    private Optional<Long> getMunicipalityId(GetPersonAddressDataRequest request, boolean isPermanent) {
        return isPermanent && request.hasPermanentMunicipalityId() ?
            Optional.of(request.getPermanentMunicipalityId()) :
            !isPermanent && request.hasTemporaryMunicipalityId() ?
                Optional.of(request.getTemporaryMunicipalityId()) : Optional.empty();
    }

    private Optional<Long> getMunicipalityPartId(GetPersonAddressDataRequest request, boolean isPermanent) {
        return isPermanent && request.hasPermanentMunicipalityPartId() ?
            Optional.of(request.getPermanentMunicipalityPartId()) :
            !isPermanent && request.hasTemporaryMunicipalityPartId() ?
                Optional.of(request.getTemporaryMunicipalityPartId()) : Optional.empty();
    }

    private Optional<Integer> getDistrictId(GetPersonAddressDataRequest request, boolean isPermanent) {
        return isPermanent && request.hasPermanentDistrictId() ?
            Optional.of(request.getPermanentDistrictId()) :
            !isPermanent && request.hasTemporaryDistrictId() ?
                Optional.of(request.getTemporaryDistrictId()) : Optional.empty();
    }

    private void setCountry(GetPersonAddressDataResponse.Builder builder, CountryValue country, boolean isPermanent) {
        if (isPermanent) {
            builder.setPermanentCountry(country);
        }
        else {
            builder.setTemporaryCountry(country);
        }
    }

    private void setMunicipality(GetPersonAddressDataResponse.Builder builder, MunicipalityValue municipality, boolean isPermanent) {
        if (isPermanent) {
            builder.setPermanentMunicipality(municipality);
        }
        else {
            builder.setTemporaryMunicipality(municipality);
        }
    }

    private void setMunicipalityPart(GetPersonAddressDataResponse.Builder builder, MunicipalityPartValue part, boolean isPermanent) {
        if (isPermanent) {
            builder.setPermanentMunicipalityPart(part);
        }
        else {
            builder.setTemporaryMunicipalityPart(part);
        }
    }

    private void setDistrict(GetPersonAddressDataResponse.Builder builder, DistrictValue district, boolean isPermanent) {
        if (isPermanent) {
            builder.setPermanentDistrict(district);
        }
        else {
            builder.setTemporaryDistrict(district);
        }
    }

    private <T> void completeResponse(StreamObserver<T> responseObserver, T response) {
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private <T> Void errorResponse(StreamObserver<T> responseObserver, Throwable ex) {
        log.error("Error processing request", ex);
        responseObserver.onError(ex);
        return null;
    }

    // Converter methods

    private CodelistEntryId toCodelistEntryId(CodelistKey key) {
        return new CodelistEntryId(key.getDomain(), key.getLowValue());
    }

    private CodelistValue toCodelistValue(CodelistEntryValue entry, String language) {
        String meaning = CZECH_LANGUAGE.equalsIgnoreCase(language) ?
            entry.getMeaningCz() : entry.getMeaningEn();

        return CodelistValue.newBuilder()
                            .setDomain(entry.getId()
                                            .getDomain())
                            .setLowValue(entry.getId()
                                              .getLowValue())
                            .setMeaning(meaning)
                            .build();
    }

    private CountryValue toCountryValue(Country country) {
        return CountryValue.newBuilder()
                           .setId(country.getId())
                           .setName(country.getName())
                           .setEnglishName(country.getEnglishName())
                           .setAbbreviation(country.getAbbreviation())
                           .build();
    }

    private CountryName toCountryName(String name) {
        return CountryName.newBuilder()
                          .setName(name)
                          .build();
    }

    private MunicipalityValue toMunicipalityValue(Municipality municipality) {
        return MunicipalityValue.newBuilder()
                                .setId(municipality.getId())
                                .setName(municipality.getName())
                                .setAbbreviation(municipality.getAbbreviation())
                                .build();
    }

    private MunicipalityPartValue toMunicipalityPartValue(MunicipalityPart municipalityPart) {
        return MunicipalityPartValue.newBuilder()
                                    .setId(municipalityPart.getId())
                                    .setName(municipalityPart.getName())
                                    .build();
    }

    private DistrictValue toDistrictValue(District district) {
        return DistrictValue.newBuilder()
                            .setId(district.getId())
                            .setName(district.getName())
                            .setAbbreviation(district.getAbbreviation())
                            .build();
    }

}
