package com.stag.identity.shared.exception;

import com.stag.identity.person.exception.PersonNotFoundException;
import grpcstarter.server.feature.exceptionhandling.annotation.GrpcAdvice;
import grpcstarter.server.feature.exceptionhandling.annotation.GrpcExceptionHandler;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@GrpcAdvice
public class GlobalGrpcExceptionHandler {

    @GrpcExceptionHandler(PersonNotFoundException.class)
    public Status handlePersonNotFoundException(PersonNotFoundException ex) {
        log.warn("gRPC request failed, person not found: {}", ex.getMessage());
        return Status.NOT_FOUND.withDescription(ex.getMessage());
    }

    @GrpcExceptionHandler(RuntimeException.class)
    public Status handleRuntimeException(RuntimeException e) {
        log.warn("gRPC request failed: {}", e.getMessage());
        return Status.INTERNAL.withDescription(e.getMessage());
    }

}
