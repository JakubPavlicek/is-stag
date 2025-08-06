package com.stag.platform.shared.grpc.service;

import com.stag.platform.address.repository.projection.AddressPlaceNameProjection;
import com.stag.platform.codelist.v1.CodelistMeaning;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
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
import com.stag.platform.education.repository.projection.HighSchoolAddressProjection;
import com.stag.platform.shared.grpc.mapper.CodelistMapper;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class CodelistGrpcService extends CodelistServiceGrpc.CodelistServiceImplBase {

    private final CodelistGrpcAsyncService asyncService;
    private final CodelistMapper codelistMapper;

    private final Executor grpcExecutor;

    // TODO: Add proper null checks and validations for request parameters (hasX() methods)
    // TODO: Add proper null checks when retrieving data from repositories
    // TODO: Errors are then handled in @GrpcAdvice of the client module

    @Override
    public void getCodelistValues(GetCodelistValuesRequest request, StreamObserver<GetCodelistValuesResponse> responseObserver) {
        CompletableFuture<List<CodelistMeaning>> codelistMeaningsFuture =
            asyncService.fetchCodelistMeaningsAsync(request.getCodelistKeysList(), request.getLanguage());

        var response = GetCodelistValuesResponse.newBuilder()
                                                .addAllCodelistMeanings(codelistMeaningsFuture.join())
                                                .build();

        completeResponse(responseObserver, response);
    }

    @Override
    public void getPersonProfileData(GetPersonProfileDataRequest request, StreamObserver<GetPersonProfileDataResponse> responseObserver) {
        CompletableFuture<List<CodelistMeaning>> codelistMeaningsFuture =
            asyncService.fetchCodelistMeaningsAsync(request.getCodelistKeysList(), request.getLanguage());

        CompletableFuture<Map<Integer, String>> countryNamesFuture =
            asyncService.fetchCountryNamesAsync(request, request.getLanguage());

        CompletableFuture.allOf(codelistMeaningsFuture, countryNamesFuture)
                         .thenApply(_ -> codelistMapper.buildPersonProfileDataResponse(
                             request,
                             codelistMeaningsFuture.join(),
                             countryNamesFuture.join()
                         ))
                         .thenAccept(response -> completeResponse(responseObserver, response))
                         .exceptionally(ex -> errorResponse(responseObserver, ex));
    }

    @Override
    public void getPersonAddressData(GetPersonAddressDataRequest request, StreamObserver<GetPersonAddressDataResponse> responseObserver) {
        CompletableFuture<Map<Long, AddressPlaceNameProjection>> addressNamesFuture =
            asyncService.fetchAddressNamesAsync(request);

        CompletableFuture<Map<Integer, String>> countryNamesFuture =
            asyncService.fetchCountryNamesAsync(request, request.getLanguage());

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
        CompletableFuture<List<CodelistMeaning>> codelistMeaningsFuture =
            asyncService.fetchCodelistMeaningsAsync(request.getCodelistKeysList(), request.getLanguage());

        CompletableFuture<Map<Integer, String>> countryNamesFuture =
            asyncService.fetchCountryNamesAsync(request, request.getLanguage());

        CompletableFuture.allOf(codelistMeaningsFuture, countryNamesFuture)
                         .thenApply(_ -> codelistMapper.buildPersonBankingDataResponse(
                             request,
                             codelistMeaningsFuture.join(),
                             countryNamesFuture.join()
                         ))
                         .thenAccept(response -> completeResponse(responseObserver, response))
                         .exceptionally(ex -> errorResponse(responseObserver, ex));
    }

    @Override
    public void getPersonEducationData(GetPersonEducationDataRequest request, StreamObserver<GetPersonEducationDataResponse> responseObserver) {
        CompletableFuture<HighSchoolAddressProjection> highSchoolAddressFuture =
            asyncService.fetchHighSchoolAddressAsync(request.hasHighSchoolId(), request.getHighSchoolId());

        CompletableFuture<String> fieldOfStudyFuture =
            asyncService.fetchHighSchoolFieldOfStudyAsync(request.hasHighSchoolFieldOfStudyNumber(), request.getHighSchoolFieldOfStudyNumber());

        CompletableFuture<Map<Integer, String>> countryNamesFuture =
            asyncService.fetchCountryNamesAsync(request, request.getLanguage());

        CompletableFuture.allOf(highSchoolAddressFuture, fieldOfStudyFuture, countryNamesFuture)
                         .thenApply(_ -> codelistMapper.buildPersonEducationDataResponse(
                             request,
                             highSchoolAddressFuture.join(),
                             fieldOfStudyFuture.join(),
                             countryNamesFuture.join()
                         ))
                         .thenAccept(response -> completeResponse(responseObserver, response))
                         .exceptionally(ex -> errorResponse(responseObserver, ex));
    }

    private <T> void completeResponse(StreamObserver<T> responseObserver, T response) {
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private <T> Void errorResponse(StreamObserver<T> responseObserver, Throwable ex) {
        log.error("Error processing request", ex.getCause());
        responseObserver.onError(ex.getCause());
        return null;
    }

}
