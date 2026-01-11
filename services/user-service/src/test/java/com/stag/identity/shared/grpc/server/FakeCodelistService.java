package com.stag.identity.shared.grpc.server;

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
import grpcstarter.server.GrpcService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.atomic.AtomicInteger;

@GrpcService("test-codelist")
public class FakeCodelistService extends CodelistServiceGrpc.CodelistServiceImplBase {

    private final AtomicInteger callCount = new AtomicInteger(0);
    private int failNextCalls = 0;
    private Status failStatus = Status.UNAVAILABLE;

    // Mock responses
    private GetCodelistValuesResponse nextCodelistValuesResponse;
    private GetPersonProfileDataResponse nextPersonProfileDataResponse;
    private GetPersonProfileUpdateDataResponse nextPersonProfileUpdateDataResponse;
    private GetPersonAddressDataResponse nextPersonAddressDataResponse;
    private GetPersonBankingDataResponse nextPersonBankingDataResponse;
    private GetPersonEducationDataResponse nextPersonEducationDataResponse;

    public void resetStats() {
        callCount.set(0);
        failNextCalls = 0;
        failStatus = Status.UNAVAILABLE;
        nextCodelistValuesResponse = null;
        nextPersonProfileDataResponse = null;
        nextPersonProfileUpdateDataResponse = null;
        nextPersonAddressDataResponse = null;
        nextPersonBankingDataResponse = null;
        nextPersonEducationDataResponse = null;
    }

    public void setFailNextCalls(int count, Status status) {
        this.failNextCalls = count;
        this.failStatus = status;
    }

    public int getCallCount() {
        return callCount.get();
    }

    public void setNextCodelistValuesResponse(GetCodelistValuesResponse response) {
        this.nextCodelistValuesResponse = response;
    }

    public void setNextPersonProfileDataResponse(GetPersonProfileDataResponse response) {
        this.nextPersonProfileDataResponse = response;
    }

    public void setNextPersonProfileUpdateDataResponse(GetPersonProfileUpdateDataResponse response) {
        this.nextPersonProfileUpdateDataResponse = response;
    }

    public void setNextPersonAddressDataResponse(GetPersonAddressDataResponse response) {
        this.nextPersonAddressDataResponse = response;
    }

    public void setNextPersonBankingDataResponse(GetPersonBankingDataResponse response) {
        this.nextPersonBankingDataResponse = response;
    }

    public void setNextPersonEducationDataResponse(GetPersonEducationDataResponse response) {
        this.nextPersonEducationDataResponse = response;
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
        if (shouldFail(responseObserver)) return;
        responseObserver.onNext(nextCodelistValuesResponse != null ? nextCodelistValuesResponse : GetCodelistValuesResponse.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getPersonProfileData(GetPersonProfileDataRequest request, StreamObserver<GetPersonProfileDataResponse> responseObserver) {
        if (shouldFail(responseObserver)) return;
        responseObserver.onNext(nextPersonProfileDataResponse != null ? nextPersonProfileDataResponse : GetPersonProfileDataResponse.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getPersonProfileUpdateData(GetPersonProfileUpdateDataRequest request, StreamObserver<GetPersonProfileUpdateDataResponse> responseObserver) {
        if (shouldFail(responseObserver)) return;
        responseObserver.onNext(nextPersonProfileUpdateDataResponse != null ? nextPersonProfileUpdateDataResponse : GetPersonProfileUpdateDataResponse.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getPersonAddressData(GetPersonAddressDataRequest request, StreamObserver<GetPersonAddressDataResponse> responseObserver) {
        if (shouldFail(responseObserver)) return;
        responseObserver.onNext(nextPersonAddressDataResponse != null ? nextPersonAddressDataResponse : GetPersonAddressDataResponse.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getPersonBankingData(GetPersonBankingDataRequest request, StreamObserver<GetPersonBankingDataResponse> responseObserver) {
        if (shouldFail(responseObserver)) return;
        responseObserver.onNext(nextPersonBankingDataResponse != null ? nextPersonBankingDataResponse : GetPersonBankingDataResponse.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getPersonEducationData(GetPersonEducationDataRequest request, StreamObserver<GetPersonEducationDataResponse> responseObserver) {
        if (shouldFail(responseObserver)) return;
        responseObserver.onNext(nextPersonEducationDataResponse != null ? nextPersonEducationDataResponse : GetPersonEducationDataResponse.getDefaultInstance());
        responseObserver.onCompleted();
    }
}