package com.stag.academics.shared.exception;

import com.stag.academics.fieldofstudy.exception.FieldOfStudyNotFoundException;
import com.stag.academics.studyprogram.exception.StudyProgramNotFoundException;
import grpcstarter.server.feature.exceptionhandling.annotation.GrpcAdvice;
import grpcstarter.server.feature.exceptionhandling.annotation.GrpcExceptionHandler;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;

/// **Global gRPC Exception Handler**
///
/// Centralized gRPC exception handler for the application. Converts domain
/// exceptions to appropriate gRPC status codes and handles runtime errors.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@GrpcAdvice
public class GlobalGrpcExceptionHandler {

    /// Handles study program not found exceptions in gRPC calls.
    ///
    /// @param ex the exception
    /// @return gRPC NOT_FOUND status with description
    @GrpcExceptionHandler(StudyProgramNotFoundException.class)
    public Status handleStudyProgramNotFoundException(StudyProgramNotFoundException ex) {
        log.warn("gRPC request failed, study program not found: {}", ex.getMessage());
        return Status.NOT_FOUND.withDescription(ex.getMessage());
    }

    /// Handles field of study not found exceptions in gRPC calls.
    ///
    /// @param ex the exception
    /// @return gRPC NOT_FOUND status with description
    @GrpcExceptionHandler(FieldOfStudyNotFoundException.class)
    public Status handleFieldOfStudyNotFoundException(FieldOfStudyNotFoundException ex) {
        log.warn("gRPC request failed, field of study not found: {}", ex.getMessage());
        return Status.NOT_FOUND.withDescription(ex.getMessage());
    }

    /// Handles all other runtime exceptions in gRPC calls.
    ///
    /// @param e the runtime exception
    /// @return gRPC INTERNAL status with description
    @GrpcExceptionHandler(RuntimeException.class)
    public Status handleRuntimeException(RuntimeException e) {
        log.warn("gRPC request failed: {}", e.getMessage());
        return Status.INTERNAL.withDescription(e.getMessage());
    }

}
