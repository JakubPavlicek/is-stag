package com.stag.platform.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class GatewayFallbackController {

    @GetMapping
    public Mono<ProblemDetail> fallback(ServerWebExchange exchange) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.SERVICE_UNAVAILABLE,
            "Service is currently unavailable. Please try again later."
        );
        problemDetail.setTitle("Service Unavailable");

        return Mono.just(problemDetail);
    }

}