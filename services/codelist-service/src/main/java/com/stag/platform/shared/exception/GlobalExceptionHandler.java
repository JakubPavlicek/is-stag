package com.stag.platform.shared.exception;

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

}
