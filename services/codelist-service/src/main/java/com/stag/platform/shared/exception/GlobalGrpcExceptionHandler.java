package com.stag.platform.shared.exception;

import com.stag.platform.address.exception.CountriesNotFoundException;
import com.stag.platform.address.exception.CountryNotFoundException;
import com.stag.platform.address.exception.MunicipalityPartsNotFoundException;
import com.stag.platform.education.exception.HighSchoolFieldOfStudyNotFoundException;
import com.stag.platform.education.exception.HighSchoolNotFoundException;
import com.stag.platform.entry.exception.CodelistEntriesNotFoundException;
import com.stag.platform.entry.exception.CodelistMeaningsNotFoundException;
import grpcstarter.server.feature.exceptionhandling.annotation.GrpcAdvice;
import grpcstarter.server.feature.exceptionhandling.annotation.GrpcExceptionHandler;
import io.grpc.Status;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@GrpcAdvice
public class GlobalGrpcExceptionHandler {

    @GrpcExceptionHandler({
        CodelistEntriesNotFoundException.class,
        CodelistMeaningsNotFoundException.class,
        CountriesNotFoundException.class,
        CountryNotFoundException.class,
        MunicipalityPartsNotFoundException.class,
        HighSchoolNotFoundException.class,
        HighSchoolFieldOfStudyNotFoundException.class
    })
    public Status handleNotFoundException(RuntimeException ex) {
        log.warn("gRPC request failed, resource not found: {}", ex.getMessage());
        return Status.NOT_FOUND.withDescription(ex.getMessage());
    }

    @GrpcExceptionHandler(RuntimeException.class)
    public Status handleRuntimeException(RuntimeException e) {
        log.warn("gRPC request failed: {}", e.getMessage());
        return Status.INTERNAL.withDescription(e.getMessage());
    }

}
