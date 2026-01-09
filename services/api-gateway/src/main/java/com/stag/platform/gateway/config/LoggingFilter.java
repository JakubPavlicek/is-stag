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
/// A reactive web filter that logs incoming HTTP requests and outgoing responses for all API endpoints (paths starting with `/api`).
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
                        logResponse(request, exchange.getResponse(), duration);
                    })
                    .doOnError(error -> {
                        Duration duration = Duration.between(start, Instant.now());
                        logError(request, error, duration);
                    });
    }

    /// Logs detailed information about an incoming HTTP request.
    ///
    /// @param request The server HTTP request to log
    private void logRequest(ServerHttpRequest request) {
        String method = request.getMethod().name();
        String path = request.getPath().value();
        String query = request.getURI().getQuery();
        String fullPath = query != null ? path + "?" + query : path;
        
        String remoteAddress = Optional.ofNullable(request.getRemoteAddress())
                                      .map(addr -> addr.getAddress().getHostAddress())
                                      .orElse("unknown");
        
        String contentType = Optional.ofNullable(request.getHeaders().getContentType())
                                     .map(MediaType::toString)
                                     .orElse("none");
        
        long contentLength = request.getHeaders().getContentLength();
        String contentLengthStr = contentLength > 0 ? contentLength + " bytes" : "none";
        
        // Detect the HTTP version from headers
        String httpVersion = request.getHeaders().containsHeader(SCHEME.text().toString()) 
            ? "HTTP/2.0" 
            : "HTTP/1.1";

        log.info("--> {} {} [{}] from {} | Content-Type: {} | Content-Length: {}",
            method, fullPath, httpVersion, remoteAddress, contentType, contentLengthStr
        );
    }

    /// Logs detailed information about an outgoing HTTP response.
    ///
    /// @param request The original server HTTP request
    /// @param response The server HTTP response to the log
    /// @param duration The time taken to process the request
    private void logResponse(ServerHttpRequest request, ServerHttpResponse response, Duration duration) {
        String method = request.getMethod().name();
        String path = request.getPath().value();
        
        int statusCode = Optional.ofNullable(response.getStatusCode())
                                 .map(HttpStatusCode::value)
                                 .orElse(-1);
        
        String statusText = getStatusText(statusCode);
        
        long millis = duration.toMillis();
        String performanceIndicator = getPerformanceIndicator(millis);
        
        String contentType = Optional.ofNullable(response.getHeaders().getContentType())
                                     .map(MediaType::toString)
                                     .orElse("none");
        
        long contentLength = response.getHeaders().getContentLength();
        String contentLengthStr = contentLength > 0 ? contentLength + " bytes" : "none";

        log.info("<-- {} {} {} {} | {}ms {} | Content-Type: {} | Content-Length: {}",
            statusCode, statusText, method, path, millis, performanceIndicator, contentType, contentLengthStr
        );
    }

    /// Logs error information when request processing fails.
    ///
    /// @param request The original server HTTP request
    /// @param error The error that occurred
    /// @param duration The time taken before the error
    private void logError(ServerHttpRequest request, Throwable error, Duration duration) {
        String method = request.getMethod().name();
        String path = request.getPath().value();
        long millis = duration.toMillis();

        log.error("<-- ERROR {} {} | {}ms | Error: {}", 
            method, path, millis, error.getMessage()
        );
    }

    /// Returns a human-readable status text for HTTP status codes.
    ///
    /// @param statusCode The HTTP status code
    /// @return Status text description
    private String getStatusText(int statusCode) {
        return switch (statusCode / 100) {
            case 2 -> "OK";
            case 3 -> "REDIRECT";
            case 4 -> "CLIENT_ERROR";
            case 5 -> "SERVER_ERROR";
            default -> "UNKNOWN";
        };
    }

    /// Returns a performance indicator emoji/symbol based on response time.
    ///
    /// @param millis Response time in milliseconds
    /// @return Performance indicator
    private String getPerformanceIndicator(long millis) {
        if (millis < 100) return "[FAST]";
        if (millis < 500) return "[OK]";
        if (millis < 1000) return "[SLOW]";
        return "[VERY_SLOW]";
    }

}