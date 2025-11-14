package com.stag.academics.shared.exception;

import com.stag.academics.student.exception.StudentNotFoundException;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@Slf4j
@GrpcAdvice
public class GlobalGrpcExceptionHandler {

    @GrpcExceptionHandler(StudentNotFoundException.class)
    public Status handleStudentNotFoundException(StudentNotFoundException ex) {
        log.warn("gRPC request failed, student not found: {}", ex.getMessage());
        return Status.NOT_FOUND.withDescription(ex.getMessage());
    }

    @GrpcExceptionHandler(RuntimeException.class)
    public Status handleRuntimeException(RuntimeException e) {
        log.warn("gRPC request failed: {}", e.getMessage());
        return Status.INTERNAL.withDescription(e.getMessage());
    }

}
