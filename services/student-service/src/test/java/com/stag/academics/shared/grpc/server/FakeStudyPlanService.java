package com.stag.academics.shared.grpc.server;

import com.stag.academics.studyplan.v1.GetStudyProgramAndFieldRequest;
import com.stag.academics.studyplan.v1.GetStudyProgramAndFieldResponse;
import com.stag.academics.studyplan.v1.StudyPlanServiceGrpc;
import grpcstarter.server.GrpcService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fake implementation of StudyPlanService for integration testing.
 */
@GrpcService
public class FakeStudyPlanService extends StudyPlanServiceGrpc.StudyPlanServiceImplBase {

    private final Map<String, GetStudyProgramAndFieldResponse> responses = new HashMap<>();

    private final AtomicInteger getStudyProgramAndFieldCallCount = new AtomicInteger(0);

    private int failNextCallsCount = 0;
    private Status failStatus = Status.INTERNAL;

    public void clear() {
        responses.clear();
        resetStats();
    }

    public void resetStats() {
        getStudyProgramAndFieldCallCount.set(0);
        failNextCallsCount = 0;
        failStatus = Status.INTERNAL;
    }

    public void addResponse(Long studyProgramId, Long studyPlanId, String language, GetStudyProgramAndFieldResponse response) {
        responses.put(makeKey(studyProgramId, studyPlanId, language), response);
    }

    public void setFailNextCalls(int count, Status status) {
        this.failNextCallsCount = count;
        this.failStatus = status;
    }

    public int getGetStudyProgramAndFieldCallCount() {
        return getStudyProgramAndFieldCallCount.get();
    }

    @Override
    public void getStudyProgramAndField(GetStudyProgramAndFieldRequest request, StreamObserver<GetStudyProgramAndFieldResponse> responseObserver) {
        getStudyProgramAndFieldCallCount.incrementAndGet();

        if (failNextCallsCount > 0) {
            failNextCallsCount--;
            responseObserver.onError(failStatus.asRuntimeException());
            return;
        }

        String key = makeKey(request.getStudyProgramId(), request.getStudyPlanId(), request.getLanguage());
        GetStudyProgramAndFieldResponse response = responses.get(key);

        if (response != null) {
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND.asRuntimeException());
        }
    }

    private String makeKey(Long studyProgramId, Long studyPlanId, String language) {
        return studyProgramId + "-" + studyPlanId + "-" + language;
    }
}
