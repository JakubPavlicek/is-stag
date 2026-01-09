package com.stag.academics.shared.grpc.service;

import com.stag.academics.shared.grpc.mapper.StudyPlanMapper;
import com.stag.academics.studyplan.service.StudyPlanService;
import com.stag.academics.studyplan.v1.GetStudyProgramAndFieldRequest;
import com.stag.academics.studyplan.v1.GetStudyProgramAndFieldResponse;
import com.stag.academics.studyplan.v1.StudyPlanServiceGrpc;
import com.stag.academics.studyprogram.service.StudyProgramService;
import grpcstarter.server.GrpcService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.StructuredTaskScope;

import static java.util.concurrent.StructuredTaskScope.Joiner.allSuccessfulOrThrow;

/// **Study Plan gRPC Service**
///
/// gRPC service implementation for study plan operations. Handles concurrent
/// fetching of study program and field of study data using async service.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RequiredArgsConstructor
@GrpcService
public class StudyPlanGrpcService extends StudyPlanServiceGrpc.StudyPlanServiceImplBase {

    /// Study Program Service
    private final StudyProgramService studyProgramService;
    /// Study Plan Service
    private final StudyPlanService studyPlanService;

    /// Retrieves study program and field of study concurrently.
    ///
    /// @param request the gRPC request with IDs and language
    /// @param responseObserver the response stream observer
    @Override
    public void getStudyProgramAndField(
        GetStudyProgramAndFieldRequest request,
        StreamObserver<GetStudyProgramAndFieldResponse> responseObserver
    ) {
        log.info("Fetching study program and field of study");

        try (var scope = StructuredTaskScope.open(allSuccessfulOrThrow())) {
            var studyProgramTask = scope.fork(
                () -> studyProgramService.findStudyProgram(request.getStudyProgramId(), request.getLanguage())
            );

            var fieldOfStudyTask = scope.fork(
                () -> studyPlanService.findFieldOfStudy(request.getStudyPlanId(), request.getLanguage())
            );

            scope.join();

            var response = StudyPlanMapper.INSTANCE.buildStudyProgramAndFieldResponse(
                studyProgramTask.get(),
                fieldOfStudyTask.get()
            );

            completeResponse(responseObserver, response);
        } catch (InterruptedException e) {
            errorResponse(responseObserver, e);
            throw new RuntimeException(e);
        }
    }

    /// Completes the gRPC response stream successfully.
    ///
    /// @param responseObserver the response stream observer
    /// @param response the response to send
    private <T> void completeResponse(StreamObserver<T> responseObserver, T response) {
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /// Handles error response in gRPC stream.
    ///
    /// @param responseObserver the response stream observer
    /// @param ex the exception that occurred
    private <T> void errorResponse(StreamObserver<T> responseObserver, Throwable ex) {
        log.error("Error processing request", ex.getCause());
        responseObserver.onError(ex.getCause());
    }

}
