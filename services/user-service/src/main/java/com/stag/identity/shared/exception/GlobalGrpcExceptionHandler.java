package com.stag.identity.shared.exception;

import com.stag.identity.person.exception.PersonNotFoundException;
import grpcstarter.server.feature.exceptionhandling.annotation.GrpcAdvice;
import grpcstarter.server.feature.exceptionhandling.annotation.GrpcExceptionHandler;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;

/// **Global gRPC Exception Handler**
///
/// Centralized exception handling for gRPC endpoints. Maps Java exceptions to
/// appropriate gRPC Status codes with descriptive messages. Handles domain-specific
/// and runtime exceptions for gRPC service implementations.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@GrpcAdvice
public class GlobalGrpcExceptionHandler {

    /// Handles person not found exceptions with gRPC NOT_FOUND status.
    ///
    /// @param ex the person not found exception
    /// @return gRPC status with NOT_FOUND code
    @GrpcExceptionHandler(PersonNotFoundException.class)
    public Status handlePersonNotFoundException(PersonNotFoundException ex) {
        log.warn("gRPC request failed, person not found: {}", ex.getMessage());
        return Status.NOT_FOUND.withDescription(ex.getMessage());
    }

    /// Handles all unhandled runtime exceptions with gRPC INTERNAL status.
    ///
    /// @param e the runtime exception
    /// @return gRPC status with INTERNAL code
    @GrpcExceptionHandler(RuntimeException.class)
    public Status handleRuntimeException(RuntimeException e) {
        log.warn("gRPC request failed: {}", e.getMessage());
        return Status.INTERNAL.withDescription(e.getMessage());
    }

}
