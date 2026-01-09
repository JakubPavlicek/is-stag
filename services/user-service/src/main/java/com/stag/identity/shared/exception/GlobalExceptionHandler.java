package com.stag.identity.shared.exception;

import com.stag.identity.person.exception.InvalidAccountNumberException;
import com.stag.identity.person.exception.InvalidBankAccountException;
import com.stag.identity.person.exception.InvalidDataBoxException;
import com.stag.identity.person.exception.PersonNotFoundException;
import com.stag.identity.person.exception.PersonProfileFetchException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/// **Global REST Exception Handler**
///
/// Centralized exception handling for REST API endpoints.
/// Converts exceptions to RFC 7807 Problem Detail responses with appropriate HTTP status codes.
/// Handles validation errors, security exceptions, gRPC errors, and circuit breaker failures.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String INVALID_VALUE = "Invalid Value";
    private static final String SERVICE_UNAVAILABLE = "Service is currently unavailable. Please try again later.";
    private static final String REQUEST_RAISED = "Request {} raised";

    /// Extracts the request URI from a Spring WebRequest.
    ///
    /// @param request the web request
    /// @return request URI string
    private String getRequestURI(WebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        return servletWebRequest.getRequest().getRequestURI();
    }

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

    /// Handles validation exceptions for invalid data (account numbers, data boxes, bank accounts).
    ///
    /// @param ex the validation exception
    /// @return problem detail with bad request status
    @ExceptionHandler({ InvalidAccountNumberException.class, InvalidDataBoxException.class, InvalidBankAccountException.class })
    public ProblemDetail handleInvalidException(Exception ex) {
        log.warn(ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle(INVALID_VALUE);

        return problemDetail;
    }

    /// Handles person not found exceptions (HTTP 404).
    ///
    /// @param ex the not found exception
    /// @return problem detail with not found status
    @ExceptionHandler(PersonNotFoundException.class)
    public ProblemDetail handlePersonNotFoundException(PersonNotFoundException ex) {
        log.warn(ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Person Not Found");

        return problemDetail;
    }

    /// Handles person profile fetch exceptions (HTTP 500).
    ///
    /// @param ex the fetch exception
    /// @return problem detail with internal server error status
    @ExceptionHandler(PersonProfileFetchException.class)
    public ProblemDetail handlePersonProfileFetchException(PersonProfileFetchException ex) {
        log.error("Profile fetch failed for personId: {}", ex.getPersonId(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        problemDetail.setTitle("Person Profile Fetch Error");

        return problemDetail;
    }

    /// Handles method argument validation failures from @Valid annotations.
    /// Returns detailed field-level error messages.
    ///
    /// @param ex the validation exception
    /// @param headers HTTP headers
    /// @param status HTTP status code
    /// @param request the web request
    /// @return problem detail with validation errors
    @Override
    protected ResponseEntity<@NonNull Object> handleMethodArgumentNotValid(
        @NonNull MethodArgumentNotValidException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request
    ) {
        log.error(REQUEST_RAISED, getRequestURI(request), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(INVALID_VALUE);
        problemDetail.setProperty("errors", getErrors(ex));

        return ResponseEntity.of(problemDetail)
                              .build();

    }

    /// Extracts field errors from validation exception and groups by field name.
    ///
    /// @param ex the validation exception
    /// @return map of field names to error messages
    private Map<String, List<String>> getErrors(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                 .getFieldErrors()
                 .stream()
                 .collect(Collectors.groupingBy(
                     FieldError::getField,
                     Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                  ));
    }

    /// Handles Jakarta Bean Validation constraint violations.
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

    /// Extracts constraint violations and groups by property path.
    ///
    /// @param ex the constraint violation exception
    /// @return map of property names to violation messages
    private Map<String, List<String>> getConstraintViolations(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                 .stream()
                 .collect(Collectors.groupingBy(
                     violation -> ((PathImpl) violation.getPropertyPath()).getLeafNode().toString(),
                     Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())
                  ));
    }

    /// Handles malformed JSON or invalid message body in HTTP requests.
    ///
    /// @param ex the message not readable exception
    /// @param headers HTTP headers
    /// @param status HTTP status code
    /// @param request the web request
    /// @return problem detail with parse error
    @Override
    protected ResponseEntity<@NonNull Object> handleHttpMessageNotReadable(
        @NonNull HttpMessageNotReadableException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request
    ) {
        log.error(REQUEST_RAISED, getRequestURI(request), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMostSpecificCause().getMessage());
        problemDetail.setTitle(INVALID_VALUE);

        return ResponseEntity.of(problemDetail).build();
    }

    /// Handles circuit breaker open state (service unavailable).
    ///
    /// @param ex the call not permitted exception
    /// @return problem detail with service unavailable status
    @ExceptionHandler(CallNotPermittedException.class)
    public ProblemDetail handleCallNotPermittedException(CallNotPermittedException ex) {
        log.error("Circuit breaker is open", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, SERVICE_UNAVAILABLE);
        problemDetail.setTitle("Service Unavailable");

        return problemDetail;
    }

    /// Handles gRPC status exceptions and maps to appropriate HTTP status codes.
    ///
    /// @param ex the gRPC status exception
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

    /// Handles all unhandled exceptions as internal server errors.
    ///
    /// @param ex the exception
    /// @param request the HTTP request
    /// @return problem detail with internal server error status
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex, HttpServletRequest request) {
        log.error(REQUEST_RAISED, request.getRequestURI(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
        problemDetail.setTitle("Internal Server Error");

        return problemDetail;
    }

    /// Maps gRPC status codes to HTTP status codes and error messages.
    ///
    /// @param grpcStatus the gRPC status
    /// @return gRPC problem detail with HTTP equivalent
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

    /// Internal record for mapping gRPC status to HTTP problem detail.
    private record GrpcProblemDetail(
        HttpStatus status,
        String title,
        String detail
    ) {

    }

}
