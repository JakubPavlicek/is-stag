package com.stag.identity.shared.grpc.server;

import com.stag.academics.student.v1.GetStudentIdsRequest;
import com.stag.academics.student.v1.GetStudentIdsResponse;
import com.stag.academics.student.v1.GetStudentPersonIdRequest;
import com.stag.academics.student.v1.GetStudentPersonIdResponse;
import com.stag.academics.student.v1.StudentServiceGrpc;
import grpcstarter.server.GrpcService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@GrpcService
public class FakeStudentService extends StudentServiceGrpc.StudentServiceImplBase {

    private final Map<Integer, List<String>> personStudentIds = new HashMap<>();
    private final Map<String, Integer> studentPersonIds = new HashMap<>();

    private final AtomicInteger getStudentIdsCallCount = new AtomicInteger(0);
    private int failNextIdsCalls = 0;
    private Status failStatus = Status.UNAVAILABLE;

    public void addStudentIds(Integer personId, List<String> studentIds) {
        personStudentIds.put(personId, studentIds);
    }

    public void addStudentPersonId(String studentId, Integer personId) {
        studentPersonIds.put(studentId, personId);
    }

    public void clear() {
        personStudentIds.clear();
        studentPersonIds.clear();
        resetStats();
    }

    public void resetStats() {
        getStudentIdsCallCount.set(0);
        failNextIdsCalls = 0;
        failStatus = Status.UNAVAILABLE;
    }

    public void setFailNextIdsCalls(int count, Status status) {
        this.failNextIdsCalls = count;
        this.failStatus = status;
    }

    public int getGetStudentIdsCallCount() {
        return getStudentIdsCallCount.get();
    }

    @Override
    public void getStudentIds(GetStudentIdsRequest request, StreamObserver<GetStudentIdsResponse> responseObserver) {
        getStudentIdsCallCount.incrementAndGet();

        if (failNextIdsCalls > 0) {
            failNextIdsCalls--;
            responseObserver.onError(failStatus.asRuntimeException());
            return;
        }

        List<String> ids = personStudentIds.get(request.getPersonId());
        if (ids != null) {
            responseObserver.onNext(GetStudentIdsResponse.newBuilder()
                                                         .addAllStudentIds(ids)
                                                         .build());
            responseObserver.onCompleted();
        }
        else {
            // Return empty list if not found, consistent with service logic
            responseObserver.onNext(GetStudentIdsResponse.newBuilder()
                                                         .addAllStudentIds(Collections.emptyList())
                                                         .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getStudentPersonId(GetStudentPersonIdRequest request, StreamObserver<GetStudentPersonIdResponse> responseObserver) {
        Integer personId = studentPersonIds.get(request.getStudentId());
        if (personId != null) {
            responseObserver.onNext(GetStudentPersonIdResponse.newBuilder()
                                                              .setPersonId(personId)
                                                              .build());
            responseObserver.onCompleted();
        }
        else {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Student not found")
                                                     .asRuntimeException());
        }
    }

}
