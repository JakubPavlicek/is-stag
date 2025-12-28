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
import com.stag.platform.codelist.v1.GetPersonProfileUpdateDataRequest;
import com.stag.platform.codelist.v1.GetPersonProfileUpdateDataResponse;
import com.stag.platform.education.repository.projection.HighSchoolAddressProjection;
import com.stag.platform.entry.service.dto.PersonProfileLowValues;
import com.stag.platform.shared.grpc.mapper.CodelistMapper;
import grpcstarter.server.GrpcService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/// **Codelist gRPC Service**
///
/// Synchronous gRPC service implementation for codelist data retrieval.
/// Orchestrates async operations for fetching person profile, address, banking, and education data.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@GrpcService
public class CodelistGrpcService extends CodelistServiceGrpc.CodelistServiceImplBase {

    /// Async service for concurrent data fetching
    private final CodelistGrpcAsyncService asyncService;

    /// Retrieves codelist meanings for provided keys.
    ///
    /// @param request Request containing codelist keys and language
    /// @param responseObserver Response stream observer
    @Override
    public void getCodelistValues(
        GetCodelistValuesRequest request,
        StreamObserver<GetCodelistValuesResponse> responseObserver
    ) {
        List<CodelistMeaning> codelistMeanings = asyncService.fetchCodelistMeanings(
            request.getCodelistKeysList(), request.getLanguage()
        );

        var response = GetCodelistValuesResponse.newBuilder()
                                                .addAllCodelistMeanings(codelistMeanings)
                                                .build();

        completeResponse(responseObserver, response);
    }

    /// Retrieves person profile data including codelist meanings and country names.
    ///
    /// @param request Request containing profile data and codelist keys
    /// @param responseObserver Response stream observer
    @Override
    public void getPersonProfileData(
        GetPersonProfileDataRequest request,
        StreamObserver<GetPersonProfileDataResponse> responseObserver
    ) {
        CompletableFuture<List<CodelistMeaning>> codelistMeaningsFuture =
            asyncService.fetchCodelistMeaningsAsync(request.getCodelistKeysList(), request.getLanguage());

        CompletableFuture<Map<Integer, String>> countryNamesFuture =
            asyncService.fetchCountryNamesAsync(request, request.getLanguage());

        CompletableFuture.allOf(codelistMeaningsFuture, countryNamesFuture)
                         .thenApply(_ -> CodelistMapper.INSTANCE.buildPersonProfileDataResponse(
                             request,
                             codelistMeaningsFuture.join(),
                             countryNamesFuture.join()
                         ))
                         .thenAccept(response -> completeResponse(responseObserver, response))
                         .exceptionally(ex -> errorResponse(responseObserver, ex));
    }

    /// Retrieves person profile update data including low values and country ID.
    ///
    /// @param request Request containing marital status, titles, and country name
    /// @param responseObserver Response stream observer
    @Override
    public void getPersonProfileUpdateData(
        GetPersonProfileUpdateDataRequest request,
        StreamObserver<GetPersonProfileUpdateDataResponse> responseObserver
    ) {
        CompletableFuture<PersonProfileLowValues> codelistLowValuesFuture =
            asyncService.fetchCodelistLowValuesAsync(request.getMaritalStatus(), request.getTitlePrefix(), request.getTitleSuffix());

        CompletableFuture<Integer> birthCountryIdFuture =
            asyncService.fetchCountryIdAsync(request.getBirthCountryName());

        CompletableFuture.allOf(codelistLowValuesFuture, birthCountryIdFuture)
                         .thenApply(_ -> CodelistMapper.INSTANCE.buildPersonProfileUpdateDataResponse(
                             codelistLowValuesFuture.join(),
                             birthCountryIdFuture.join()
                         ))
                         .thenAccept(response -> completeResponse(responseObserver, response))
                         .exceptionally(ex -> errorResponse(responseObserver, ex));
    }

    /// Retrieves person address data including municipality and country information.
    ///
    /// @param request Request containing address IDs and language
    /// @param responseObserver Response stream observer
    @Override
    public void getPersonAddressData(
        GetPersonAddressDataRequest request,
        StreamObserver<GetPersonAddressDataResponse> responseObserver
    ) {
        CompletableFuture<Map<Long, AddressPlaceNameProjection>> addressNamesFuture =
            asyncService.fetchAddressNamesAsync(request);

        CompletableFuture<Map<Integer, String>> countryNamesFuture =
            asyncService.fetchCountryNamesAsync(request, request.getLanguage());

        CompletableFuture.allOf(addressNamesFuture, countryNamesFuture)
                         .thenApply(_ -> CodelistMapper.INSTANCE.buildPersonAddressDataResponse(
                             request,
                             addressNamesFuture.join(),
                             countryNamesFuture.join()
                         ))
                         .thenAccept(response -> completeResponse(responseObserver, response))
                         .exceptionally(ex -> errorResponse(responseObserver, ex));
    }

    /// Retrieves person banking data including codelist meanings and country name.
    ///
    /// @param request Request containing banking data and codelist keys
    /// @param responseObserver Response stream observer
    @Override
    public void getPersonBankingData(
        GetPersonBankingDataRequest request,
        StreamObserver<GetPersonBankingDataResponse> responseObserver
    ) {
        CompletableFuture<List<CodelistMeaning>> codelistMeaningsFuture =
            asyncService.fetchCodelistMeaningsAsync(request.getCodelistKeysList(), request.getLanguage());

        CompletableFuture<Map<Integer, String>> countryNamesFuture =
            asyncService.fetchCountryNamesAsync(request, request.getLanguage());

        CompletableFuture.allOf(codelistMeaningsFuture, countryNamesFuture)
                         .thenApply(_ -> CodelistMapper.INSTANCE.buildPersonBankingDataResponse(
                             request,
                             codelistMeaningsFuture.join(),
                             countryNamesFuture.join()
                         ))
                         .thenAccept(response -> completeResponse(responseObserver, response))
                         .exceptionally(ex -> errorResponse(responseObserver, ex));
    }

    /// Retrieves person education data including high school and field of study information.
    ///
    /// @param request Request containing education IDs and language
    /// @param responseObserver Response stream observer
    @Override
    public void getPersonEducationData(
        GetPersonEducationDataRequest request,
        StreamObserver<GetPersonEducationDataResponse> responseObserver
    ) {
        CompletableFuture<HighSchoolAddressProjection> highSchoolAddressFuture =
            asyncService.fetchHighSchoolAddressAsync(request.hasHighSchoolId(), request.getHighSchoolId());

        CompletableFuture<String> fieldOfStudyFuture =
            asyncService.fetchHighSchoolFieldOfStudyAsync(request.hasHighSchoolFieldOfStudyNumber(), request.getHighSchoolFieldOfStudyNumber());

        CompletableFuture<Map<Integer, String>> countryNamesFuture =
            asyncService.fetchCountryNamesAsync(request, request.getLanguage());

        CompletableFuture.allOf(highSchoolAddressFuture, fieldOfStudyFuture, countryNamesFuture)
                         .thenApply(_ -> CodelistMapper.INSTANCE.buildPersonEducationDataResponse(
                             request,
                             highSchoolAddressFuture.join(),
                             fieldOfStudyFuture.join(),
                             countryNamesFuture.join()
                         ))
                         .thenAccept(response -> completeResponse(responseObserver, response))
                         .exceptionally(ex -> errorResponse(responseObserver, ex));
    }

    /// Completes the gRPC response by sending the result and marking the stream as completed.
    ///
    /// @param responseObserver Response stream observer
    /// @param response Response to send
    private <T> void completeResponse(StreamObserver<T> responseObserver, T response) {
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /// Handles errors by logging and sending error status to the client.
    ///
    /// @param responseObserver Response stream observer
    /// @param ex Exception that occurred
    /// @return null
    private <T> Void errorResponse(StreamObserver<T> responseObserver, Throwable ex) {
        log.error("Error processing request", ex.getCause());
        responseObserver.onError(ex.getCause());
        return null;
    }

}
