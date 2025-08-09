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

    @GrpcExceptionHandler({
        CodelistEntriesNotFoundException.class,
        CountriesNotFoundException.class,
        MunicipalityPartsNotFoundException.class,
        HighSchoolNotFoundException.class,
        HighSchoolFieldOfStudyNotFoundException.class
    })
    public Status handleResourceNotFoundException(RuntimeException e) {
        log.warn("gRPC request failed, resource not found: {}", e.getMessage());
        return Status.NOT_FOUND.withDescription(e.getMessage());
    }

}
