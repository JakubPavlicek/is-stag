package com.stag.platform.gateway.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.InetSocketAddress;
import java.net.URI;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoggingFilterTest {

    private final LoggingFilter filter = new LoggingFilter();

    @Test
    @DisplayName("should skip logging for non-API requests")
    void filter_NonApiRequest_SkipsLogging() {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        WebFilterChain chain = mock(WebFilterChain.class);

        when(exchange.getRequest()).thenReturn(request);
        when(request.getURI()).thenReturn(URI.create("/actuator/health"));
        when(request.getPath()).thenReturn(RequestPath.parse("/actuator/health", "/"));
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                    .verifyComplete();

        // Verify chain continues
        verify(chain).filter(exchange);
    }

    @Test
    @DisplayName("should log API requests and continue chain")
    void filter_ApiRequest_LogsAndContinues() {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        WebFilterChain chain = mock(WebFilterChain.class);
        HttpHeaders headers = new HttpHeaders();

        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getURI()).thenReturn(URI.create("/api/v1/test"));
        when(request.getPath()).thenReturn(RequestPath.parse("/api/v1/test", "/"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getRemoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 1234));
        when(request.getHeaders()).thenReturn(headers);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        when(response.getHeaders()).thenReturn(headers);
        
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                    .verifyComplete();

        verify(chain).filter(exchange);
    }
}
