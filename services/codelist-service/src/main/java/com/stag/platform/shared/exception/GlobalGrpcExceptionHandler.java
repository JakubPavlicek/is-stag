package com.stag.platform.shared.exception;

import com.stag.platform.address.exception.CountriesNotFoundException;
import com.stag.platform.address.exception.MunicipalityPartsNotFoundException;
import com.stag.platform.education.exception.HighSchoolFieldOfStudyNotFoundException;
import com.stag.platform.education.exception.HighSchoolNotFoundException;
import com.stag.platform.entry.exception.CodelistEntriesNotFoundException;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;

@Slf4j
@GrpcAdvice
public class GlobalGrpcExceptionHandler {

    @GrpcExceptionHandler(CodelistEntriesNotFoundException.class)
    public Status handleCodelistEntriesNotFoundException(CodelistEntriesNotFoundException ex) {
        log.warn("gRPC request failed, codelist entries not found: {}", ex.getMessage());
        return Status.NOT_FOUND.withDescription(ex.getMessage());
    }

    @GrpcExceptionHandler(CountriesNotFoundException.class)
    public Status handleCountriesNotFoundException(CountriesNotFoundException ex) {
        log.warn("gRPC request failed, countries not found: {}", ex.getMessage());
        return Status.NOT_FOUND.withDescription(ex.getMessage());
    }

    @GrpcExceptionHandler(MunicipalityPartsNotFoundException.class)
    public Status handleMunicipalityPartsNotFoundException(MunicipalityPartsNotFoundException ex) {
        log.warn("gRPC request failed, municipality parts not found: {}", ex.getMessage());
        return Status.NOT_FOUND.withDescription(ex.getMessage());
    }

    @GrpcExceptionHandler(HighSchoolNotFoundException.class)
    public Status handleHighSchoolNotFoundException(HighSchoolNotFoundException ex) {
        log.warn("gRPC request failed, high school not found: {}", ex.getMessage());
        return Status.NOT_FOUND.withDescription(ex.getMessage());
    }

    @GrpcExceptionHandler(HighSchoolFieldOfStudyNotFoundException.class)
    public Status handleHighSchoolFieldOfStudyNotFoundException(HighSchoolFieldOfStudyNotFoundException ex) {
        log.warn("gRPC request failed, field of study not found: {}", ex.getMessage());
        return Status.NOT_FOUND.withDescription(ex.getMessage());
    }

    @GrpcExceptionHandler(RuntimeException.class)
    public Status handleRuntimeException(RuntimeException e) {
        log.warn("gRPC request failed: {}", e.getMessage());
        return Status.INTERNAL.withDescription(e.getMessage());
    }

}
