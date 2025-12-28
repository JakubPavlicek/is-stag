package com.stag.academics.shared.exception;

import com.stag.academics.student.exception.StudentNotFoundException;
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

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String INVALID_VALUE = "Invalid Value";
    private static final String SERVICE_UNAVAILABLE = "Service is currently unavailable. Please try again later.";
    private static final String REQUEST_RAISED = "Request {} raised";

    private String getRequestURI(WebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        return servletWebRequest.getRequest().getRequestURI();
    }

    @ExceptionHandler(StudentNotFoundException.class)
    public ProblemDetail handleStudentNotFoundException(StudentNotFoundException ex) {
        log.warn(ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Student Not Found");

        return problemDetail;
    }

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

    private Map<String, List<String>> getErrors(MethodArgumentNotValidException ex) {
        return ex.getBindingResult()
                 .getFieldErrors()
                 .stream()
                 .collect(Collectors.groupingBy(
                     FieldError::getField,
                     Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                 ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        log.error(REQUEST_RAISED, request.getRequestURI(), e);

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle(INVALID_VALUE);
        problemDetail.setProperty("violations", getConstraintViolations(e));

        return problemDetail;
    }

    private Map<String, List<String>> getConstraintViolations(ConstraintViolationException ex) {
        return ex.getConstraintViolations()
                 .stream()
                 .collect(Collectors.groupingBy(
                     violation -> ((PathImpl) violation.getPropertyPath()).getLeafNode().toString(),
                     Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())
                 ));
    }

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

    @ExceptionHandler(CallNotPermittedException.class)
    public ProblemDetail handleCallNotPermittedException(CallNotPermittedException ex) {
        log.error("Circuit breaker is open", ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, SERVICE_UNAVAILABLE);
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

    @ExceptionHandler({CompletionException.class, ExecutionException.class})
    public ProblemDetail handleAsyncWrapperExceptions(Exception ex, HttpServletRequest request) {
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

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex, HttpServletRequest request) {
        log.error(REQUEST_RAISED, request.getRequestURI(), ex);

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
        problemDetail.setTitle("Internal Server Error");

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
            case UNAVAILABLE -> new GrpcProblemDetail(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", SERVICE_UNAVAILABLE);
            case DEADLINE_EXCEEDED -> new GrpcProblemDetail(HttpStatus.GATEWAY_TIMEOUT, "Gateway Timeout", SERVICE_UNAVAILABLE);
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
