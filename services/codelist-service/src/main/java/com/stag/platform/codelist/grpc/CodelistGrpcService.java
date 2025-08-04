package com.stag.platform.codelist.grpc;

import com.stag.platform.codelist.entity.CodelistEntryId;
import com.stag.platform.codelist.grpc.mapper.CodelistMapper;
import com.stag.platform.codelist.repository.projection.AddressPlaceNameProjection;
import com.stag.platform.codelist.repository.projection.CodelistEntryValue;
import com.stag.platform.codelist.repository.projection.HighSchoolAddressProjection;
import com.stag.platform.codelist.service.CodelistService;
import com.stag.platform.codelist.service.CountryService;
import com.stag.platform.codelist.service.HighSchoolFieldOfStudyService;
import com.stag.platform.codelist.service.HighSchoolService;
import com.stag.platform.codelist.service.MunicipalityPartService;
import com.stag.platform.codelist.v1.CodelistKey;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import com.stag.platform.codelist.v1.CodelistValue;
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
    private final HighSchoolService highSchoolService;
    private final HighSchoolFieldOfStudyService highSchoolFieldOfStudyService;

    private final Executor grpcExecutor;
    private final CodelistMapper codelistMapper;

    // TODO: Add proper null checks and validations for request parameters (hasX() methods)
    // TODO: Add proper null checks when retrieving data from repositories
    // TODO: Errors are then handled in @GrpcAdvice of the client module

    @Override
    public void getCodelistValues(GetCodelistValuesRequest request, StreamObserver<GetCodelistValuesResponse> responseObserver) {
        List<CodelistValue> codelistValues = fetchCodelistValues(request.getCodelistKeysList(), request.getLanguage());

        var response = GetCodelistValuesResponse.newBuilder()
                                                .addAllCodelistValues(codelistValues)
                                                .build();

        completeResponse(responseObserver, response);
    }

    // TODO: Consider using @Async instead of grpcExecutor for better integration with Spring's async capabilities
    @Override
    public void getPersonProfileData(GetPersonProfileDataRequest request, StreamObserver<GetPersonProfileDataResponse> responseObserver) {
        CompletableFuture<List<CodelistValue>> codelistValuesFuture = CompletableFuture.supplyAsync(
            () -> fetchCodelistValues(request.getCodelistKeysList(), request.getLanguage()),
            grpcExecutor
        );

        CompletableFuture<Map<Integer, String>> countryNamesFuture = CompletableFuture.supplyAsync(
            () -> fetchCountryNames(codelistMapper.extractCountryIds(request), request.getLanguage()),
            grpcExecutor
        );

        CompletableFuture.allOf(codelistValuesFuture, countryNamesFuture)
                         .thenApply(_ -> codelistMapper.buildPersonProfileDataResponse(
                             request,
                             codelistValuesFuture.join(),
                             countryNamesFuture.join()
                         ))
                         .thenAccept(response -> completeResponse(responseObserver, response))
                         .exceptionally(ex -> errorResponse(responseObserver, ex));
    }

    @Override
    public void getPersonAddressData(GetPersonAddressDataRequest request, StreamObserver<GetPersonAddressDataResponse> responseObserver) {
        CompletableFuture<Map<Long, AddressPlaceNameProjection>> addressNamesFuture = CompletableFuture.supplyAsync(
            () -> fetchAddressNames(codelistMapper.extractMunicipalityPartIds(request)),
            grpcExecutor
        );

        CompletableFuture<Map<Integer, String>> countryNamesFuture = CompletableFuture.supplyAsync(
            () -> fetchCountryNames(codelistMapper.extractCountryIds(request), request.getLanguage()),
            grpcExecutor
        );

        CompletableFuture.allOf(addressNamesFuture, countryNamesFuture)
                         .thenApply(_ -> codelistMapper.buildPersonAddressDataResponse(
                             request,
                             addressNamesFuture.join(),
                             countryNamesFuture.join()
                         ))
                         .thenAccept(response -> completeResponse(responseObserver, response))
                         .exceptionally(ex -> errorResponse(responseObserver, ex));
    }

    @Override
    public void getPersonBankingData(GetPersonBankingDataRequest request, StreamObserver<GetPersonBankingDataResponse> responseObserver) {
        CompletableFuture<List<CodelistValue>> codelistValuesFuture = CompletableFuture.supplyAsync(
            () -> fetchCodelistValues(request.getCodelistKeysList(), request.getLanguage()),
            grpcExecutor
        );

        CompletableFuture<Map<Integer, String>> countryNamesFuture = CompletableFuture.supplyAsync(
            () -> fetchCountryNames(codelistMapper.extractCountryIds(request), request.getLanguage()),
            grpcExecutor
        );

        CompletableFuture.allOf(codelistValuesFuture, countryNamesFuture)
                         .thenApply(_ -> codelistMapper.buildPersonBankingDataResponse(
                             request,
                             codelistValuesFuture.join(),
                             countryNamesFuture.join()
                         ))
                         .thenAccept(response -> completeResponse(responseObserver, response))
                         .exceptionally(ex -> errorResponse(responseObserver, ex));
    }

    @Override
    public void getPersonEducationData(GetPersonEducationDataRequest request, StreamObserver<GetPersonEducationDataResponse> responseObserver) {
        CompletableFuture<HighSchoolAddressProjection> highSchoolFuture = CompletableFuture.supplyAsync(
            () -> fetchHighSchool(request.hasHighSchoolId(), request.getHighSchoolId()),
            grpcExecutor
        );

        CompletableFuture<String> fieldOfStudyFuture = CompletableFuture.supplyAsync(
            () -> fetchHighSchoolFieldOfStudy(request.hasHighSchoolFieldOfStudyNumber(), request.getHighSchoolFieldOfStudyNumber()),
            grpcExecutor
        );

        CompletableFuture<Map<Integer, String>> countryNamesFuture = CompletableFuture.supplyAsync(
            () -> fetchCountryNames(codelistMapper.extractCountryIds(request), request.getLanguage()),
            grpcExecutor
        );

        CompletableFuture.allOf(highSchoolFuture, fieldOfStudyFuture, countryNamesFuture)
                         .thenApply(_ -> codelistMapper.buildPersonEducationDataResponse(
                             request,
                             highSchoolFuture.join(),
                             fieldOfStudyFuture.join(),
                             countryNamesFuture.join()
                         ))
                         .thenAccept(response -> completeResponse(responseObserver, response))
                         .exceptionally(ex -> errorResponse(responseObserver, ex));
    }

    private HighSchoolAddressProjection fetchHighSchool(boolean hasHighSchoolId, String highSchoolId) {
        return hasHighSchoolId
            ? highSchoolService.getHighSchoolName(highSchoolId)
            : null;
    }

    private String fetchHighSchoolFieldOfStudy(boolean hasFieldOfStudyNumber, String fieldOfStudyNumber) {
        return hasFieldOfStudyNumber
            ? highSchoolFieldOfStudyService.findFieldOfStudyName(fieldOfStudyNumber)
            : null;
    }

    private List<CodelistValue> fetchCodelistValues(List<CodelistKey> codelistKeys, String language) {
        if (codelistKeys.isEmpty()) {
            return Collections.emptyList();
        }

        List<CodelistEntryId> entryIds = codelistMapper.extractCodelistEntryIds(codelistKeys);
        List<CodelistEntryValue> entries = codelistService.findCodelistEntriesByIds(entryIds, language);
        return codelistMapper.mapToCodelistValues(entries);
    }

    private Map<Integer, String> fetchCountryNames(Set<Integer> countryIds, String language) {
        return countryIds.isEmpty()
            ? Collections.emptyMap()
            : countryService.findNamesByIds(countryIds, language);
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
