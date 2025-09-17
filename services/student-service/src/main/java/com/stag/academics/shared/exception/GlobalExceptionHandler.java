package com.stag.academics.shared.exception;

import com.stag.academics.student.exception.StudentNotFoundException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
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

    @ExceptionHandler(StudentNotFoundException.class)
    public ProblemDetail handleStudentNotFoundException(StudentNotFoundException ex) {
        log.warn(ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Student not found");

        return problemDetail;
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ProblemDetail handleCallNotPermittedException(CallNotPermittedException ex) {
        log.error("Circuit breaker is open", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.SERVICE_UNAVAILABLE,
            "Service is currently unavailable. Please try again later."
        );
        problemDetail.setTitle("Service Unavailable");

        return problemDetail;
    }

    @ExceptionHandler(StatusRuntimeException.class)
    public ProblemDetail handleGrpcException(StatusRuntimeException ex) {
        log.warn("gRPC call failed: {}", ex.getMessage());

        GrpcProblemDetail grpcProblemDetail = toGrpcProblemDetail(ex.getStatus());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(grpcProblemDetail.status, grpcProblemDetail.detail);
        problemDetail.setTitle(grpcProblemDetail.title);

        return problemDetail;
    }

    private GrpcProblemDetail toGrpcProblemDetail(Status grpcStatus) {
        return switch (grpcStatus.getCode()) {
            case NOT_FOUND -> new GrpcProblemDetail(HttpStatus.NOT_FOUND, "Resource Not Found", "Resource not found");
            case INVALID_ARGUMENT -> new GrpcProblemDetail(HttpStatus.BAD_REQUEST, "Invalid Argument", "Invalid argument");
            case PERMISSION_DENIED -> new GrpcProblemDetail(HttpStatus.FORBIDDEN, "Permission Denied", "Permission denied");
            case UNAUTHENTICATED -> new GrpcProblemDetail(HttpStatus.UNAUTHORIZED, "Unauthorized", "Unauthorized");
            case ALREADY_EXISTS -> new GrpcProblemDetail(HttpStatus.CONFLICT, "Already Exists", "Already exists");
            case FAILED_PRECONDITION -> new GrpcProblemDetail(HttpStatus.PRECONDITION_FAILED, "Failed Precondition", "Failed precondition");
            case UNIMPLEMENTED -> new GrpcProblemDetail(HttpStatus.NOT_IMPLEMENTED, "Unimplemented", "Unimplemented");
            case UNAVAILABLE -> new GrpcProblemDetail(HttpStatus.SERVICE_UNAVAILABLE, "Upstream Service Unavailable", "Upstream service unavailable");
            case DEADLINE_EXCEEDED -> new GrpcProblemDetail(HttpStatus.GATEWAY_TIMEOUT, "Upstream Service Timeout", "Upstream service timeout");
            default -> new GrpcProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Internal server error");
        };
    }

    private record GrpcProblemDetail(
        HttpStatus status,
        String title,
        String detail
    ) {

    }

}
