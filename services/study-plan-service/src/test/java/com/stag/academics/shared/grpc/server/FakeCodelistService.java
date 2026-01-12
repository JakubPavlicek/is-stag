package com.stag.academics.shared.grpc.server;

import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import com.stag.platform.codelist.v1.GetCodelistValuesRequest;
import com.stag.platform.codelist.v1.GetCodelistValuesResponse;
import grpcstarter.server.GrpcService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

@GrpcService
public class FakeCodelistService extends CodelistServiceGrpc.CodelistServiceImplBase {

    private final AtomicInteger callCount = new AtomicInteger(0);
    private int failNextCalls = 0;
    private Status failStatus = Status.UNAVAILABLE;

    @Setter
    private GetCodelistValuesResponse nextCodelistValuesResponse;

    public void resetStats() {
        callCount.set(0);
        failNextCalls = 0;
        failStatus = Status.UNAVAILABLE;
        nextCodelistValuesResponse = null;
    }

    public void setFailNextCalls(int count, Status status) {
        this.failNextCalls = count;
        this.failStatus = status;
    }

    public int getCallCount() {
        return callCount.get();
    }

    private boolean shouldFail(StreamObserver<?> responseObserver) {
        callCount.incrementAndGet();
        if (failNextCalls > 0) {
            failNextCalls--;
            responseObserver.onError(failStatus.asRuntimeException());
            return true;
        }
        return false;
    }

    @Override
    public void getCodelistValues(GetCodelistValuesRequest request, StreamObserver<GetCodelistValuesResponse> responseObserver) {
        if (shouldFail(responseObserver)) {
            return;
        }
        responseObserver.onNext(nextCodelistValuesResponse != null ? nextCodelistValuesResponse : GetCodelistValuesResponse.getDefaultInstance());
        responseObserver.onCompleted();
    }
}
