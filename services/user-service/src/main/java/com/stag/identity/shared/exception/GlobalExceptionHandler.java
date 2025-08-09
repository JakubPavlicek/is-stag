package com.stag.identity.shared.exception;

import com.stag.identity.person.exception.PersonNotFoundException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(PersonNotFoundException.class)
    public ProblemDetail handlePersonNotFoundException(PersonNotFoundException ex) {
        log.error(ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Person not found");

        return problemDetail;
    }

    @ExceptionHandler(StatusRuntimeException.class)
    public ProblemDetail handleGrpcException(StatusRuntimeException ex) {
        log.error("gRPC call failed: {}", ex.getMessage());

        Status grpcStatus = ex.getStatus();
        HttpStatus httpStatus = mapGrpcStatusToHttp(grpcStatus);
        String description = grpcStatus.getDescription() != null
            ? grpcStatus.getDescription()
            : "gRPC service error";

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, description);
        problemDetail.setTitle("Service communication error");

        return problemDetail;
    }

    private HttpStatus mapGrpcStatusToHttp(Status grpcStatus) {
        return switch (grpcStatus.getCode()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
            case PERMISSION_DENIED -> HttpStatus.FORBIDDEN;
            case UNAUTHENTICATED -> HttpStatus.UNAUTHORIZED;
            case ALREADY_EXISTS -> HttpStatus.CONFLICT;
            case FAILED_PRECONDITION -> HttpStatus.PRECONDITION_FAILED;
            case UNIMPLEMENTED -> HttpStatus.NOT_IMPLEMENTED;
            case UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case DEADLINE_EXCEEDED -> HttpStatus.REQUEST_TIMEOUT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

}
