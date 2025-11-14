package com.stag.academics.shared.exception;

import com.stag.academics.fieldofstudy.exception.FieldOfStudyNotFoundException;
import com.stag.academics.studyprogram.exception.StudyProgramNotFoundException;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@Slf4j
@GrpcAdvice
public class GlobalGrpcExceptionHandler {

    @GrpcExceptionHandler(StudyProgramNotFoundException.class)
    public Status handleStudyProgramNotFoundException(StudyProgramNotFoundException ex) {
        log.warn("gRPC request failed, study program not found: {}", ex.getMessage());
        return Status.NOT_FOUND.withDescription(ex.getMessage());
    }

    @GrpcExceptionHandler(FieldOfStudyNotFoundException.class)
    public Status handleFieldOfStudyNotFoundException(FieldOfStudyNotFoundException ex) {
        log.warn("gRPC request failed, field of study not found: {}", ex.getMessage());
        return Status.NOT_FOUND.withDescription(ex.getMessage());
    }

    @GrpcExceptionHandler(RuntimeException.class)
    public Status handleRuntimeException(RuntimeException e) {
        log.warn("gRPC request failed: {}", e.getMessage());
        return Status.INTERNAL.withDescription(e.getMessage());
    }

}
