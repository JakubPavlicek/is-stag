package com.stag.platform.codelist.grpc;

import com.stag.platform.codelist.entity.CodelistEntryId;
import com.stag.platform.codelist.grpc.mapper.CodelistMapper;
import com.stag.platform.codelist.repository.projection.AddressPlaceNameProjection;
import com.stag.platform.codelist.repository.projection.CodelistEntryValue;
import com.stag.platform.codelist.service.CodelistService;
import com.stag.platform.codelist.service.CountryService;
import com.stag.platform.codelist.service.MunicipalityPartService;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CodelistGrpcService extends CodelistServiceGrpc.CodelistServiceImplBase {

    private final CodelistService codelistService;
    private final CountryService countryService;
    private final MunicipalityPartService municipalityPartService;

    private final Executor grpcExecutor;
    private final CodelistMapper mapper;

    @Override
    public void getCodelistValues(GetCodelistValuesRequest request, StreamObserver<GetCodelistValuesResponse> responseObserver) {
        List<CodelistValue> codelistValues = buildCodelistValues(request.getCodelistKeysList(), request.getLanguage());

        var response = GetCodelistValuesResponse.newBuilder()
                                                .addAllCodelistValues(codelistValues)
                                                .build();

        completeResponse(responseObserver, response);
    }

    // TODO: Consider using @Async instead of grpcExecutor for better integration with Spring's async capabilities
    @Override
    public void getPersonProfileData(GetPersonProfileDataRequest request, StreamObserver<GetPersonProfileDataResponse> responseObserver) {
        CompletableFuture<List<CodelistValue>> codelistValuesFuture = CompletableFuture.supplyAsync(
            () -> {
                log.info("codelistValuesFuture thread: {}", Thread.currentThread());
                return buildCodelistValues(request.getCodelistKeysList(), request.getLanguage());
            },
            grpcExecutor
        );

        CompletableFuture<Map<Integer, String>> countryNamesFuture = CompletableFuture.supplyAsync(
            () -> {
                log.info("countryNamesFuture thread: {}", Thread.currentThread());
                return fetchCountryNames(mapper.extractCountryIds(request));
            },
            grpcExecutor
        );

        codelistValuesFuture.thenCombine(countryNamesFuture, (codelistValues, countryNames) ->
                                mapper.buildPersonProfileDataResponse(request, codelistValues, countryNames)
                            )
                            .thenAccept(response -> completeResponse(responseObserver, response))
                            .exceptionally(ex -> errorResponse(responseObserver, ex));
    }

    @Override
    public void getPersonAddressData(GetPersonAddressDataRequest request, StreamObserver<GetPersonAddressDataResponse> responseObserver) {
        CompletableFuture<Map<Long, AddressPlaceNameProjection>> addressNamesFuture = CompletableFuture.supplyAsync(
            () -> {
                log.info("addressNamesFuture thread: {}", Thread.currentThread());
                return fetchAddressNames(mapper.extractMunicipalityPartIds(request));
            },
            grpcExecutor
        );

        CompletableFuture<Map<Integer, String>> countryNamesFuture = CompletableFuture.supplyAsync(
            () -> {
                log.info("countryNamesFuture thread: {}", Thread.currentThread());
                return fetchCountryNames(mapper.extractCountryIds(request));
            },
            grpcExecutor
        );

        addressNamesFuture.thenCombine(countryNamesFuture, (addressNames, countryNames) ->
                              mapper.buildPersonAddressDataResponse(request, addressNames, countryNames)
                          )
                          .thenAccept(response -> completeResponse(responseObserver, response))
                          .exceptionally(ex -> errorResponse(responseObserver, ex));
    }

    private List<CodelistValue> buildCodelistValues(List<CodelistKey> codelistKeys, String language) {
        if (codelistKeys.isEmpty()) {
            return Collections.emptyList();
        }

        List<CodelistEntryId> entryIds = mapper.extractCodelistEntryIds(codelistKeys);
        List<CodelistEntryValue> entries = codelistService.getCodelistEntryMeanings(entryIds);
        return mapper.mapToCodelistValues(entries, language);
    }

    private Map<Integer, String> fetchCountryNames(Set<Integer> countryIds) {
        return countryIds.isEmpty()
            ? Collections.emptyMap()
            : countryService.findNamesByIds(countryIds);
    }

    private Map<Long, AddressPlaceNameProjection> fetchAddressNames(Set<Long> municipalityPartIds) {
        return municipalityPartIds.isEmpty()
            ? Collections.emptyMap()
            : municipalityPartService.findAddressNamesByIds(municipalityPartIds);
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

}
