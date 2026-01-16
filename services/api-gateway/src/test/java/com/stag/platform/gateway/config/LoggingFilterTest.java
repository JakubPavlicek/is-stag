package com.stag.platform.gateway.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;

import static io.netty.handler.codec.http2.HttpConversionUtil.ExtensionHeaderNames.SCHEME;
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

    @Test
    @DisplayName("should log error when chain fails")
    void filter_ApiRequest_OnError_LogsError() {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        WebFilterChain chain = mock(WebFilterChain.class);
        HttpHeaders headers = new HttpHeaders();

        when(exchange.getRequest()).thenReturn(request);
        when(request.getURI()).thenReturn(URI.create("/api/v1/error"));
        when(request.getPath()).thenReturn(RequestPath.parse("/api/v1/error", "/"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getRemoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 1234));
        when(request.getHeaders()).thenReturn(headers);

        when(chain.filter(exchange)).thenReturn(Mono.error(new RuntimeException("Test Error")));

        StepVerifier.create(filter.filter(exchange, chain))
                    .expectError(RuntimeException.class)
                    .verify();
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 302, 400, 500, 999})
    @DisplayName("should process different status codes")
    void filter_ApiRequest_DifferentStatusCodes_RunsThrough(int statusCode) {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        WebFilterChain chain = mock(WebFilterChain.class);
        HttpHeaders headers = new HttpHeaders();

        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getURI()).thenReturn(URI.create("/api/v1/status/" + statusCode));
        when(request.getPath()).thenReturn(RequestPath.parse("/api/v1/status/" + statusCode, "/"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getRemoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 1234));
        when(request.getHeaders()).thenReturn(headers);

        when(response.getStatusCode()).thenReturn(HttpStatusCode.valueOf(statusCode));
        when(response.getHeaders()).thenReturn(headers);

        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                    .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(longs = {50, 200, 600, 1100})
    @DisplayName("should process different response durations")
    void filter_ApiRequest_DifferentDurations_RunsThrough(long delayMillis) {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        WebFilterChain chain = mock(WebFilterChain.class);
        HttpHeaders headers = new HttpHeaders();

        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getURI()).thenReturn(URI.create("/api/v1/perf"));
        when(request.getPath()).thenReturn(RequestPath.parse("/api/v1/perf", "/"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getRemoteAddress()).thenReturn(new InetSocketAddress("127.0.0.1", 1234));
        when(request.getHeaders()).thenReturn(headers);

        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        when(response.getHeaders()).thenReturn(headers);

        // We use Mono.delay to simulate time passing. 
        // Since LoggingFilter uses Instant.now(), we need real time to pass.
        when(chain.filter(exchange)).thenReturn(Mono.delay(Duration.ofMillis(delayMillis)).then());

        StepVerifier.create(filter.filter(exchange, chain))
                    .verifyComplete();
    }

    @Test
    @DisplayName("should log unknown remote address and none content-type/length")
    void filter_ApiRequest_MissingHeadersAndRemoteAddress_LogsDefaults() {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        WebFilterChain chain = mock(WebFilterChain.class);
        HttpHeaders headers = new HttpHeaders();

        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getURI()).thenReturn(URI.create("/api/v1/defaults"));
        when(request.getPath()).thenReturn(RequestPath.parse("/api/v1/defaults", "/"));
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getRemoteAddress()).thenReturn(null);
        when(request.getHeaders()).thenReturn(headers);
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        when(response.getHeaders()).thenReturn(headers);
        
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                    .verifyComplete();
    }

    @Test
    @DisplayName("should log HTTP/2.0 and explicit content-type/length")
    void filter_ApiRequest_Http2AndFullHeaders_LogsCorrectly() {
        ServerWebExchange exchange = mock(ServerWebExchange.class);
        ServerHttpRequest request = mock(ServerHttpRequest.class);
        ServerHttpResponse response = mock(ServerHttpResponse.class);
        WebFilterChain chain = mock(WebFilterChain.class);
        
        HttpHeaders reqHeaders = new HttpHeaders();
        reqHeaders.setContentType(MediaType.APPLICATION_JSON);
        reqHeaders.setContentLength(500);
        reqHeaders.add(SCHEME.text().toString(), "https");
        
        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.APPLICATION_JSON);
        respHeaders.setContentLength(1000);

        when(exchange.getRequest()).thenReturn(request);
        when(exchange.getResponse()).thenReturn(response);
        when(request.getURI()).thenReturn(URI.create("/api/v1/full"));
        when(request.getPath()).thenReturn(RequestPath.parse("/api/v1/full", "/"));
        when(request.getMethod()).thenReturn(HttpMethod.POST);
        when(request.getRemoteAddress()).thenReturn(new InetSocketAddress("10.0.0.1", 80));
        when(request.getHeaders()).thenReturn(reqHeaders);
        
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        when(response.getHeaders()).thenReturn(respHeaders);
        
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(filter.filter(exchange, chain))
                    .verifyComplete();
    }
}
