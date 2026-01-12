package com.stag.academics.shared.grpc.server;

import com.stag.identity.person.v1.GetPersonSimpleProfileRequest;
import com.stag.identity.person.v1.GetPersonSimpleProfileResponse;
import com.stag.identity.person.v1.PersonServiceGrpc;
import grpcstarter.server.GrpcService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@GrpcService
public class FakePersonService extends PersonServiceGrpc.PersonServiceImplBase {

    private final Map<Integer, GetPersonSimpleProfileResponse> profiles = new HashMap<>();

    private final AtomicInteger getPersonSimpleProfileCallCount = new AtomicInteger(0);

    private int failNextCallsCount = 0;
    private Status failStatus = Status.INTERNAL;

    public void clear() {
        profiles.clear();
        resetStats();
    }

    public void resetStats() {
        getPersonSimpleProfileCallCount.set(0);
        failNextCallsCount = 0;
        failStatus = Status.INTERNAL;
    }

    public void addProfile(Integer personId, GetPersonSimpleProfileResponse response) {
        profiles.put(personId, response);
    }

    public void setFailNextCalls(int count, Status status) {
        this.failNextCallsCount = count;
        this.failStatus = status;
    }

    public int getGetPersonSimpleProfileCallCount() {
        return getPersonSimpleProfileCallCount.get();
    }

    @Override
    public void getPersonSimpleProfile(GetPersonSimpleProfileRequest request, StreamObserver<GetPersonSimpleProfileResponse> responseObserver) {
        getPersonSimpleProfileCallCount.incrementAndGet();

        if (failNextCallsCount > 0) {
            failNextCallsCount--;
            responseObserver.onError(failStatus.asRuntimeException());
            return;
        }

        GetPersonSimpleProfileResponse response = profiles.get(request.getPersonId());
        if (response != null) {
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());
        }
    }
}
