package com.stag.identity.shared.grpc.service;

import com.stag.identity.person.model.SimpleProfile;
import com.stag.identity.person.service.ProfileService;
import com.stag.identity.person.v1.GetPersonSimpleProfileRequest;
import com.stag.identity.person.v1.GetPersonSimpleProfileResponse;
import com.stag.identity.person.v1.PersonServiceGrpc;
import com.stag.identity.shared.grpc.mapper.PersonMapper;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class PersonGrpcService extends PersonServiceGrpc.PersonServiceImplBase {

    private final ProfileService profileService;

    @Override
    public void getPersonSimpleProfile(
        GetPersonSimpleProfileRequest request,
        StreamObserver<GetPersonSimpleProfileResponse> responseObserver
    ) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        throw Status.INTERNAL.asRuntimeException();
//        SimpleProfile simpleProfile = profileService.getPersonSimpleProfile(
//            request.getPersonId(), request.getLanguage()
//        );
//
//        var response = PersonMapper.INSTANCE.buildPersonSimpleProfileResponse(simpleProfile);
//
//        completeResponse(responseObserver, response);
    }

    private <T> void completeResponse(StreamObserver<T> responseObserver, T response) {
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
