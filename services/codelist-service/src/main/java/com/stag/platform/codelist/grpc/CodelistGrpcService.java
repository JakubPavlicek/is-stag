package com.stag.platform.codelist.grpc;

import com.stag.platform.codelist.entity.CodelistEntryId;
import com.stag.platform.codelist.repository.projection.AddressPlaceNameProjection;
import com.stag.platform.codelist.repository.projection.CodelistEntryValue;
import com.stag.platform.codelist.service.CodelistService;
import com.stag.platform.codelist.service.CountryService;
import com.stag.platform.codelist.service.DistrictService;
import com.stag.platform.codelist.service.MunicipalityPartService;
import com.stag.platform.codelist.service.MunicipalityService;
import com.stag.platform.codelist.v1.CodelistKey;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import com.stag.platform.codelist.v1.CodelistValue;
import com.stag.platform.codelist.v1.GetCodelistValuesRequest;
import com.stag.platform.codelist.v1.GetCodelistValuesResponse;
import com.stag.platform.codelist.v1.GetPersonAddressDataRequest;
import com.stag.platform.codelist.v1.GetPersonAddressDataResponse;
import com.stag.platform.codelist.v1.GetPersonProfileDataRequest;
import com.stag.platform.codelist.v1.GetPersonProfileDataResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CodelistGrpcService extends CodelistServiceGrpc.CodelistServiceImplBase {

    private static final String CZECH_LANGUAGE = "cs";

    private final Executor grpcExecutor;
    private final CodelistService codelistService;
    private final CountryService countryService;
    private final MunicipalityService municipalityService;
    private final MunicipalityPartService municipalityPartService;
    private final DistrictService districtService;

    @Override
    public void getCodelistValues(GetCodelistValuesRequest request, StreamObserver<GetCodelistValuesResponse> responseObserver) {
        List<CodelistValue> codelistValues = buildCodelistValues(request.getCodelistKeysList(), request.getLanguage());

        var response = GetCodelistValuesResponse.newBuilder()
                                                .addAllCodelistValues(codelistValues)
                                                .build();

        completeResponse(responseObserver, response);
    }

    // TODO: check Virtual Thread usage
    @Override
    public void getPersonProfileData(GetPersonProfileDataRequest request, StreamObserver<GetPersonProfileDataResponse> responseObserver) {
        CompletableFuture<List<CodelistValue>> codelistValuesFuture = CompletableFuture.supplyAsync(
            () -> buildCodelistValues(request.getCodelistKeysList(), request.getLanguage()),
            grpcExecutor
        );

        CompletableFuture<Map<Integer, String>> countryNamesFuture = CompletableFuture.supplyAsync(
            () -> getCountryNames(request),
            grpcExecutor
        );

        codelistValuesFuture.thenCombine(countryNamesFuture, (codelistValues, countryNames) ->
                                buildPersonProfileDataResponse(request, codelistValues, countryNames)
                            )
                            .thenAccept(response -> completeResponse(responseObserver, response))
                            .exceptionally(ex -> errorResponse(responseObserver, ex));
    }

    // TODO: check Virtual Thread usage
    @Override
    public void getPersonAddressData(GetPersonAddressDataRequest request, StreamObserver<GetPersonAddressDataResponse> responseObserver) {
        CompletableFuture<Map<Long, AddressPlaceNameProjection>> addressNamesFuture = CompletableFuture.supplyAsync(
            () -> getAddressNames(request),
            grpcExecutor
        );

        CompletableFuture<Map<Integer, String>> countryNamesFuture = CompletableFuture.supplyAsync(
            () -> getCountryNames(request),
            grpcExecutor
        );

        addressNamesFuture.thenCombine(countryNamesFuture, (addressNames, countryNames) ->
                                buildPersonAddressDataResponse(request, addressNames, countryNames)
                            )
                            .thenAccept(response -> completeResponse(responseObserver, response))
                            .exceptionally(ex -> errorResponse(responseObserver, ex));
    }

    // Private helper methods

    private List<CodelistValue> buildCodelistValues(List<CodelistKey> codelistKeys, String language) {
        if (codelistKeys.isEmpty()) {
            return Collections.emptyList();
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
        Set<Integer> countryIds = HashSet.newHashSet(2);

        if (request.hasBirthCountryId()) {
            countryIds.add(request.getBirthCountryId());
        }
        if (request.hasCitizenshipCountryId()) {
            countryIds.add(request.getCitizenshipCountryId());
        }

        if (countryIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return countryService.findNamesByIds(countryIds);
    }

    private Map<Integer, String> getCountryNames(GetPersonAddressDataRequest request) {
        Set<Integer> countryIds = HashSet.newHashSet(2);

        if (request.hasPermanentCountryId()) {
            countryIds.add(request.getPermanentCountryId());
        }
        if (request.hasTemporaryCountryId()) {
            countryIds.add(request.getTemporaryCountryId());
        }

        if (countryIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return countryService.findNamesByIds(countryIds);
    }

    private Map<Long, AddressPlaceNameProjection> getAddressNames(GetPersonAddressDataRequest request) {
        Set<Long> municipalityPartIds = HashSet.newHashSet(2);

        if (request.hasPermanentMunicipalityPartId()) {
            municipalityPartIds.add(request.getPermanentMunicipalityPartId());
        }
        if (request.hasTemporaryMunicipalityPartId()) {
            municipalityPartIds.add(request.getTemporaryMunicipalityPartId());
        }

        if (municipalityPartIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return municipalityPartService.findAddressNamesByIds(municipalityPartIds);
    }

    private GetPersonProfileDataResponse buildPersonProfileDataResponse(
        GetPersonProfileDataRequest request,
        List<CodelistValue> codelistValues,
        Map<Integer, String> countryNames
    ) {
        var responseBuilder = GetPersonProfileDataResponse.newBuilder().addAllCodelistValues(codelistValues);

        if (!countryNames.isEmpty()) {
            processCountryName(countryNames, request.getBirthCountryId(), responseBuilder::setBirthCountryName);
            processCountryName(countryNames, request.getCitizenshipCountryId(), responseBuilder::setCitizenshipCountryName);
        }

        return responseBuilder.build();
    }

    private GetPersonAddressDataResponse buildPersonAddressDataResponse(
        GetPersonAddressDataRequest request,
        Map<Long, AddressPlaceNameProjection> addressNames,
        Map<Integer, String> countryNames
    ) {
        var responseBuilder = GetPersonAddressDataResponse.newBuilder();

        if (!addressNames.isEmpty()) {
            AddressPlaceNameProjection permanentAddress = addressNames.get(request.getPermanentMunicipalityPartId());
            if (permanentAddress != null) {
                responseBuilder.setPermanentMunicipalityName(permanentAddress.municipalityName())
                               .setPermanentMunicipalityPartName(permanentAddress.municipalityPartName())
                               .setPermanentDistrictName(permanentAddress.districtName());
            }

            AddressPlaceNameProjection temporaryAddress = addressNames.get(request.getTemporaryMunicipalityPartId());
            if (temporaryAddress != null) {
                responseBuilder.setTemporaryMunicipalityName(temporaryAddress.municipalityName())
                               .setTemporaryMunicipalityPartName(temporaryAddress.municipalityPartName())
                               .setTemporaryDistrictName(temporaryAddress.districtName());
            }
        }

        if (!countryNames.isEmpty()) {
            processCountryName(countryNames, request.getPermanentCountryId(), responseBuilder::setPermanentCountryName);
            processCountryName(countryNames, request.getTemporaryCountryId(), responseBuilder::setTemporaryCountryName);
        }

        return responseBuilder.build();
    }

    private void processCountryName(
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
        String meaning = CZECH_LANGUAGE.equalsIgnoreCase(language)
            ? entry.getMeaningCz()
            : entry.getMeaningEn();

        return CodelistValue.newBuilder()
                            .setDomain(entry.getId().getDomain())
                            .setLowValue(entry.getId().getLowValue())
                            .setMeaning(meaning)
                            .build();
    }

}
