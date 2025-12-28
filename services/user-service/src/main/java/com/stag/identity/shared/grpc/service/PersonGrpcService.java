package com.stag.identity.shared.grpc.service;

import com.stag.identity.person.model.SimpleProfile;
import com.stag.identity.person.service.ProfileService;
import com.stag.identity.person.v1.GetPersonSimpleProfileRequest;
import com.stag.identity.person.v1.GetPersonSimpleProfileResponse;
import com.stag.identity.person.v1.PersonServiceGrpc;
import com.stag.identity.shared.grpc.mapper.PersonMapper;
import grpcstarter.server.GrpcService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/// **Person gRPC Service**
///
/// gRPC service implementation for person-related operations. Provides RPC endpoints
/// for retrieving person profiles with localized data. Used for inter-service
/// communication within the microservice architecture.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@GrpcService
public class PersonGrpcService extends PersonServiceGrpc.PersonServiceImplBase {

    /// Profile Service
    private final ProfileService profileService;

    /// Retrieves a simple person profile via gRPC with localized codelist values.
    ///
    /// @param request the gRPC request with person ID and language
    /// @param responseObserver the response stream observer
    @Override
    public void getPersonSimpleProfile(
        GetPersonSimpleProfileRequest request,
        StreamObserver<GetPersonSimpleProfileResponse> responseObserver
    ) {
        SimpleProfile simpleProfile = profileService.getPersonSimpleProfile(
            request.getPersonId(), request.getLanguage()
        );

        var response = PersonMapper.INSTANCE.buildPersonSimpleProfileResponse(simpleProfile);

        completeResponse(responseObserver, response);
    }

    /// Completes a gRPC response stream with the given response object.
    ///
    /// @param responseObserver the response stream observer
    /// @param response the response object to send
    /// @param <T> response type
    private <T> void completeResponse(StreamObserver<T> responseObserver, T response) {
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
