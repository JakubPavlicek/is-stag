package com.stag.academics.shared.exception;

import com.stag.academics.student.exception.StudentNotFoundException;
import grpcstarter.server.feature.exceptionhandling.annotation.GrpcAdvice;
import grpcstarter.server.feature.exceptionhandling.annotation.GrpcExceptionHandler;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;

/// **Global gRPC Exception Handler**
///
/// Centralized exception handler for gRPC service calls.
/// Converts Java exceptions to appropriate gRPC status codes.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@GrpcAdvice
public class GlobalGrpcExceptionHandler {

    /// Handles a student not found exceptions in gRPC calls.
    ///
    /// @param ex the student not found exception
    /// @return gRPC NOT_FOUND status with description
    @GrpcExceptionHandler(StudentNotFoundException.class)
    public Status handleStudentNotFoundException(StudentNotFoundException ex) {
        log.warn("gRPC request failed, student not found: {}", ex.getMessage());
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
