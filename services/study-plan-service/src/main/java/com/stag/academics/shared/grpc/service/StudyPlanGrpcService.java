package com.stag.academics.shared.grpc.service;

import com.stag.academics.fieldofstudy.repository.projection.FieldOfStudyView;
import com.stag.academics.shared.grpc.mapper.StudyPlanMapper;
import com.stag.academics.studyplan.v1.GetStudyProgramAndFieldRequest;
import com.stag.academics.studyplan.v1.GetStudyProgramAndFieldResponse;
import com.stag.academics.studyplan.v1.StudyPlanServiceGrpc;
import com.stag.academics.studyprogram.repository.projection.StudyProgramView;
import grpcstarter.server.GrpcService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@GrpcService
public class StudyPlanGrpcService extends StudyPlanServiceGrpc.StudyPlanServiceImplBase {

    private final StudyPlanAsyncGrpcService asyncService;

    @Override
    public void getStudyProgramAndField(
        GetStudyProgramAndFieldRequest request,
        StreamObserver<GetStudyProgramAndFieldResponse> responseObserver
    ) {
        CompletableFuture<StudyProgramView> studyProgramFuture =
            asyncService.fetchStudyProgram(request.getStudyProgramId(), request.getLanguage());
        CompletableFuture<FieldOfStudyView> fieldOfStudyFuture =
            asyncService.fetchFieldOfStudy(request.getStudyPlanId(), request.getLanguage());

        CompletableFuture.allOf(studyProgramFuture, fieldOfStudyFuture)
                         .thenApply(_ -> StudyPlanMapper.INSTANCE.buildStudyProgramAndFieldResponse(
                             studyProgramFuture.join(),
                             fieldOfStudyFuture.join()
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
