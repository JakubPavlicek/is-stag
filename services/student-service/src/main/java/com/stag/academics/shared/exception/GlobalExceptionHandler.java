package com.stag.academics.shared.exception;

import com.stag.academics.student.exception.StudentNotFoundException;
import com.stag.academics.student.exception.StudentProfileFetchException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/// **Global Exception Handler**
///
/// Centralized REST API exception handler for the Student service.
/// Handles validation errors, business exceptions, gRPC errors, circuit breaker failures,
/// and converts them to RFC 7807 Problem Detail responses.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String INVALID_VALUE = "Invalid Value";
    private static final String SERVICE_UNAVAILABLE = "Service is currently unavailable. Please try again later.";
    private static final String REQUEST_RAISED = "Request {} raised";

    /// Handles Spring Security access denied exceptions (HTTP 403).
    ///
    /// @param ex the access denied exception
    /// @return problem detail with forbidden status
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        log.warn(ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problemDetail.setTitle("Access Denied");

        return problemDetail;
    }

    /// Handles a student not found exceptions.
    ///
    /// @param ex the student not found exception
    /// @return problem detail with 404 status
    @ExceptionHandler(StudentNotFoundException.class)
    public ProblemDetail handleStudentNotFoundException(StudentNotFoundException ex) {
        log.warn(ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Student Not Found");

        return problemDetail;
    }

    /// Handles student profile fetch exceptions (HTTP 500).
    ///
    /// @param ex the fetch exception
    /// @return problem detail with internal server error status
    @ExceptionHandler(StudentProfileFetchException.class)
    public ProblemDetail handleStudentProfileFetchException(StudentProfileFetchException ex) {
        log.error("Profile fetch failed for studentId: {}", ex.getStudentId(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setTitle("Student Profile Fetch Error");

        return problemDetail;
    }

    /// Handles constraint violation exceptions.
    ///
    /// @param e the constraint violation exception
    /// @param request the HTTP request
    /// @return problem detail with constraint violations
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        log.error(REQUEST_RAISED, request.getRequestURI(), e);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle(INVALID_VALUE);
        problemDetail.setProperty("violations", getConstraintViolations(e));

        return problemDetail;
    }

    /// Extracts constraint violations from exception.
    ///
    /// @param ex the constraint violation exception
    /// @return map of property paths to violation messages
    private Map<String, List<String>> getConstraintViolations(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                 .stream()
                 .collect(Collectors.groupingBy(
                     violation -> ((PathImpl) violation.getPropertyPath()).getLeafNode().toString(),
                     Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())
                 ));
    }

    /// Handles circuit breaker open state exceptions.
    ///
    /// Returns 503 Service Unavailable when the circuit breaker prevents calls.
    ///
    /// @param ex the circuit breaker exception
    /// @return problem detail with status 503
    @ExceptionHandler(CallNotPermittedException.class)
    public ProblemDetail handleCallNotPermittedException(CallNotPermittedException ex) {
        log.error("Circuit breaker is open", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, SERVICE_UNAVAILABLE);
        problemDetail.setTitle("Service Unavailable");

        return problemDetail;
    }

    /// Handles gRPC status exceptions and maps to HTTP status codes.
    ///
    /// @param ex the gRPC status runtime exception
    /// @return problem detail with mapped HTTP status
    @ExceptionHandler(StatusRuntimeException.class)
    public ProblemDetail handleGrpcException(StatusRuntimeException ex) {
        log.warn("gRPC call failed: {}", ex.getMessage());

        GrpcProblemDetail grpcProblemDetail = toGrpcProblemDetail(ex.getStatus());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(grpcProblemDetail.status, grpcProblemDetail.detail);
        problemDetail.setTitle(grpcProblemDetail.title);

        return problemDetail;
    }

    /// Handles concurrent exceptions by unwrapping and delegating to specific handlers.
    ///
    /// @param ex the concurrent exception
    /// @param request the HTTP request
    /// @return problem detail based on underlying cause
    @ExceptionHandler({ CompletionException.class, ExecutionException.class })
    public ProblemDetail handleConcurrentExceptions(Exception ex, HttpServletRequest request) {
        // Special handling for gRPC exceptions
        if (ex.getCause() instanceof StatusRuntimeException sre) {
            return handleGrpcException(sre);
        }
        // Special handling for Circuit Breaker exceptions
        if (ex.getCause() instanceof CallNotPermittedException cnpe) {
            return handleCallNotPermittedException(cnpe);
        }

        return handleException(ex, request);
    }

    /// Handles all other unhandled exceptions.
    ///
    /// @param ex the exception
    /// @param request the HTTP request
    /// @return problem detail with 500 status
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex, HttpServletRequest request) {
        log.error(REQUEST_RAISED, request.getRequestURI(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
        problemDetail.setTitle("Internal Server Error");

        return problemDetail;
    }

    /// Maps gRPC status codes to HTTP status codes with titles and details.
    ///
    /// @param grpcStatus the gRPC status
    /// @return mapped HTTP problem detail
    private GrpcProblemDetail toGrpcProblemDetail(Status grpcStatus) {
        return switch (grpcStatus.getCode()) {
            case NOT_FOUND -> new GrpcProblemDetail(HttpStatus.NOT_FOUND, "Resource Not Found", "Resource not found");
            case INVALID_ARGUMENT -> new GrpcProblemDetail(HttpStatus.BAD_REQUEST, "Invalid Argument", "Invalid argument");
            case PERMISSION_DENIED -> new GrpcProblemDetail(HttpStatus.FORBIDDEN, "Permission Denied", "Permission denied");
            case UNAUTHENTICATED -> new GrpcProblemDetail(HttpStatus.UNAUTHORIZED, "Unauthorized", "Unauthorized");
            case ALREADY_EXISTS -> new GrpcProblemDetail(HttpStatus.CONFLICT, "Already Exists", "Already exists");
            case FAILED_PRECONDITION -> new GrpcProblemDetail(HttpStatus.PRECONDITION_FAILED, "Failed Precondition", "Failed precondition");
            case UNIMPLEMENTED -> new GrpcProblemDetail(HttpStatus.NOT_IMPLEMENTED, "Unimplemented", "Unimplemented");
            case UNAVAILABLE -> new GrpcProblemDetail(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", SERVICE_UNAVAILABLE);
            case DEADLINE_EXCEEDED -> new GrpcProblemDetail(HttpStatus.GATEWAY_TIMEOUT, "Gateway Timeout", SERVICE_UNAVAILABLE);
            default -> new GrpcProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Internal server error");
        };
    }

    /// Internal record for mapping gRPC status to HTTP problem details.
    private record GrpcProblemDetail(
        HttpStatus status,
        String title,
        String detail
    ) {

    }

}
