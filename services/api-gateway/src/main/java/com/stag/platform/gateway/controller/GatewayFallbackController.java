package com.stag.platform.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class GatewayFallbackController {

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