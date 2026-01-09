package com.stag.platform.shared.grpc.service;

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
import com.stag.platform.shared.grpc.mapper.CodelistMapper;
import grpcstarter.server.GrpcService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.StructuredTaskScope;

import static java.util.concurrent.StructuredTaskScope.Joiner.allSuccessfulOrThrow;

/// **Codelist gRPC Service**
///
/// Synchronous gRPC service implementation for codelist data retrieval.
/// Orchestrates operations for fetching a person's profile, address, banking, and education data.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@GrpcService
public class CodelistGrpcService extends CodelistServiceGrpc.CodelistServiceImplBase {

    /// Codelist Data Service for data fetching
    private final CodelistDataService dataService;

    /// Retrieves codelist meanings for provided keys.
    ///
    /// @param request Request containing codelist keys and language
    /// @param responseObserver Response stream observer
    @Override
    public void getCodelistValues(
        GetCodelistValuesRequest request,
        StreamObserver<GetCodelistValuesResponse> responseObserver
    ) {
        log.info("Fetching codelist values");

        List<CodelistMeaning> codelistMeanings = dataService.fetchCodelistMeanings(
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
        log.info("Fetching person profile data");

        try (var scope = StructuredTaskScope.open(allSuccessfulOrThrow())) {
            var codelistMeaningsTask = scope.fork(
                () -> dataService.fetchCodelistMeanings(request.getCodelistKeysList(), request.getLanguage())
            );

            var countryNamesTask = scope.fork(
                () -> dataService.fetchCountryNames(request, request.getLanguage())
            );

            scope.join();

            var response = CodelistMapper.INSTANCE.buildPersonProfileDataResponse(
                request,
                codelistMeaningsTask.get(),
                countryNamesTask.get()
            );

            completeResponse(responseObserver, response);
        } catch (StructuredTaskScope.FailedException e) {
            errorResponse(responseObserver, e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            errorResponse(responseObserver, e);
        }
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
        log.info("Fetching person profile update data");

        try (var scope = StructuredTaskScope.open(allSuccessfulOrThrow())) {
            var codelistLowValuesTask = scope.fork(
                () -> dataService.fetchCodelistLowValues(
                    request.getMaritalStatus(), request.getTitlePrefix(), request.getTitleSuffix()
                )
            );

            var birthCountryIdTask = scope.fork(
                () -> dataService.fetchCountryId(request.getBirthCountryName())
            );

            scope.join();

            var response = CodelistMapper.INSTANCE.buildPersonProfileUpdateDataResponse(
                codelistLowValuesTask.get(),
                birthCountryIdTask.get()
            );

            completeResponse(responseObserver, response);
        } catch (StructuredTaskScope.FailedException e) {
            errorResponse(responseObserver, e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            errorResponse(responseObserver, e);
        }
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
        log.info("Fetching person address data");

        try (var scope = StructuredTaskScope.open(allSuccessfulOrThrow())) {
            var addressNamesTask = scope.fork(
                () -> dataService.fetchAddressNames(request)
            );

            var countryNamesTask = scope.fork(
                () -> dataService.fetchCountryNames(request, request.getLanguage())
            );

            scope.join();

            var response = CodelistMapper.INSTANCE.buildPersonAddressDataResponse(
                request,
                addressNamesTask.get(),
                countryNamesTask.get()
            );

            completeResponse(responseObserver, response);
        } catch (StructuredTaskScope.FailedException e) {
            errorResponse(responseObserver, e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            errorResponse(responseObserver, e);
        }
    }

    /// Retrieves person's banking data including codelist meanings and country name.
    ///
    /// @param request Request containing banking data and codelist keys
    /// @param responseObserver Response stream observer
    @Override
    public void getPersonBankingData(
        GetPersonBankingDataRequest request,
        StreamObserver<GetPersonBankingDataResponse> responseObserver
    ) {
        log.info("Fetching person banking data");

        try (var scope = StructuredTaskScope.open(allSuccessfulOrThrow())) {
            var codelistMeaningsTask = scope.fork(
                () -> dataService.fetchCodelistMeanings(request.getCodelistKeysList(), request.getLanguage())
            );

            var countryNamesTask = scope.fork(
                () -> dataService.fetchCountryNames(request, request.getLanguage())
            );

            scope.join();

            var response = CodelistMapper.INSTANCE.buildPersonBankingDataResponse(
                request,
                codelistMeaningsTask.get(),
                countryNamesTask.get()
            );

            completeResponse(responseObserver, response);
        } catch (StructuredTaskScope.FailedException e) {
            errorResponse(responseObserver, e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            errorResponse(responseObserver, e);
        }
    }

    /// Retrieves person's education data including high school and field of study information.
    ///
    /// @param request Request containing education IDs and language
    /// @param responseObserver Response stream observer
    @Override
    public void getPersonEducationData(
        GetPersonEducationDataRequest request,
        StreamObserver<GetPersonEducationDataResponse> responseObserver
    ) {
        log.info("Fetching person education data");

        try (var scope = StructuredTaskScope.open(allSuccessfulOrThrow())) {
            var highSchoolAddressTask = scope.fork(
                () -> dataService.fetchHighSchoolAddress(request.hasHighSchoolId(), request.getHighSchoolId())
            );

            var fieldOfStudyTask = scope.fork(
                () -> dataService.fetchHighSchoolFieldOfStudy(
                    request.hasHighSchoolFieldOfStudyNumber(), request.getHighSchoolFieldOfStudyNumber()
                )
            );

            var countryNamesTask = scope.fork(
                () -> dataService.fetchCountryNames(request, request.getLanguage())
            );

            scope.join();

            var response = CodelistMapper.INSTANCE.buildPersonEducationDataResponse(
                request,
                highSchoolAddressTask.get(),
                fieldOfStudyTask.get(),
                countryNamesTask.get()
            );

            completeResponse(responseObserver, response);
        } catch (StructuredTaskScope.FailedException e) {
            errorResponse(responseObserver, e.getCause());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            errorResponse(responseObserver, e);
        }
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
    private <T> void errorResponse(StreamObserver<T> responseObserver, Throwable ex) {
        log.error("Error processing request", ex.getCause());
        responseObserver.onError(ex.getCause());
    }

}
