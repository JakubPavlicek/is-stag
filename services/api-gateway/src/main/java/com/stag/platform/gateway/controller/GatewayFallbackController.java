package com.stag.platform.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// **Gateway Fallback Controller**
///
/// Provides a fallback endpoint for Spring Cloud Gateway when backend services are
/// unavailable or unreachable. This controller is invoked when circuit breakers trip
/// or when routes fail to connect to downstream services.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@RestController
@RequestMapping("/fallback")
public class GatewayFallbackController {

    /// Handles fallback requests when backend services are unavailable.
    ///
    /// This endpoint is invoked by Spring Cloud Gateway when:
    /// - Circuit breaker is open (too many failures)
    /// - Backend service is down or unreachable
    /// - Request timeout occurs
    /// - Network errors prevent connection
    ///
    /// @return ProblemDetail with 503 status and error message
    @GetMapping
    public ProblemDetail fallback() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.SERVICE_UNAVAILABLE,
            "Service is currently unavailable. Please try again later."
        );
        problemDetail.setTitle("Service Unavailable");

        return problemDetail;
    }

}