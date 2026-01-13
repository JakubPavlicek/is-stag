package com.stag.platform.gateway.controller;

import com.stag.platform.gateway.config.PerftestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(GatewayFallbackController.class)
@Import(PerftestSecurityConfig.class)
@ActiveProfiles("perftest")
class GatewayFallbackControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    @DisplayName("should return 503 Service Unavailable with ProblemDetail")
    void fallback_Returns503() {
        webClient.get()
                 .uri("/fallback")
                 .accept(MediaType.APPLICATION_JSON)
                 .exchange()
                 .expectStatus().isEqualTo(503)
                 .expectBody()
                 .jsonPath("$.status").isEqualTo(503)
                 .jsonPath("$.title").isEqualTo("Service Unavailable")
                 .jsonPath("$.detail").isEqualTo("Service is currently unavailable. Please try again later.");
    }
}
