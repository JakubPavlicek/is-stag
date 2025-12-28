package com.stag.platform.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static io.netty.handler.codec.http2.HttpConversionUtil.ExtensionHeaderNames.SCHEME;

/// **HTTP Request/Response Logging Filter**
///
/// A reactive web filter that logs incoming HTTP requests and outgoing responses
/// for all API endpoints (paths starting with `/api`).
///
/// **Request Information**
/// - HTTP method (GET, POST, etc.)
/// - Request path and query parameters
/// - HTTP version (1.1 or 2.0)
/// - Content-Type header
/// - Content-Length
///
/// **Response Information**
/// - HTTP status code
/// - Response duration in milliseconds
/// - Content-Type
/// - Content-Length
///
/// Non-API requests are skipped to reduce log noise.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Slf4j
@Component
public class LoggingFilter implements WebFilter {

    /// Filters and logs HTTP requests and responses for API endpoints.
    ///
    /// The filter:
    /// 1. Checks if the request path starts with `/api`
    /// 2. Logs request details (method, path, content type, etc.)
    /// 3. Records the start time
    /// 4. Proceeds with the filter chain
    /// 5. Logs response details and duration on completion
    ///
    /// @param exchange The current server web exchange
    /// @param chain The filter chain to execute
    /// @return Mono that completes when the filter chain finishes
    @Override
    @NonNull
    public Mono<@NonNull Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Skip logging for non-API requests (e.g., actuator, static resources)
        if (!request.getURI().getPath().startsWith("/api")) {
            return chain.filter(exchange);
        }

        // Log the incoming request
        logRequest(request);
        
        // Record start time for duration calculation
        Instant start = Instant.now();

        // Continue with the filter chain and log response on completion
        return chain.filter(exchange)
                    .doOnSuccess(_ -> {
                        Duration duration = Duration.between(start, Instant.now());
                        logResponse(exchange.getResponse(), duration);
                    });
    }

    /// Logs detailed information about an incoming HTTP request.
    ///
    /// Logged information includes:
    /// - HTTP method and path
    /// - Query parameters
    /// - HTTP version (1.1 or 2.0)
    /// - Content-Type header
    /// - Content-Length
    ///
    /// @param request The server HTTP request to log
    private void logRequest(ServerHttpRequest request) {
        String method = request.getMethod().name();
        String path = request.getPath().value();
        String query = request.getURI().getQuery();
        String contentType = Optional.ofNullable(request.getHeaders().getContentType())
                                     .map(MediaType::toString)
                                     .orElse("");
        long contentLength = request.getHeaders().getContentLength();
        
        // Detect the HTTP version from headers
        String httpVersion = "HTTP/1.1";
        if (request.getHeaders().containsHeader(SCHEME.text().toString())) {
            httpVersion = "HTTP/2.0";
        }

        log.debug("REQUEST: {} {}{} {} contentType={} contentLength={}",
            method, path, query, httpVersion, contentType, contentLength
        );
    }

    /// Logs detailed information about an outgoing HTTP response.
    ///
    /// Logged information includes:
    /// - HTTP status code
    /// - Request duration in milliseconds
    /// - Content-Type
    /// - Content-Length
    ///
    /// @param response The server HTTP response to log
    /// @param duration The time taken to process the request
    private void logResponse(ServerHttpResponse response, Duration duration) {
        int status = Optional.ofNullable(response.getStatusCode())
                             .map(HttpStatusCode::value)
                             .orElse(-1);
        long millis = duration.toMillis();
        String contentType = Optional.ofNullable(response.getHeaders().getContentType())
                                     .map(MediaType::getType)
                                     .orElse("");
        long contentLength = response.getHeaders().getContentLength();

        log.debug("RESPONSE: {} duration={}ms contentType={} contentLength={}",
            status, millis, contentType, contentLength
        );
    }

}