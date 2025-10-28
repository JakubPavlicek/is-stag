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

@Slf4j
@Component
public class LoggingFilter implements WebFilter {

    @Override
    @NonNull
    public Mono<@NonNull Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // Skip logging for non-API requests
        if (!request.getURI().getPath().startsWith("/api")) {
            return chain.filter(exchange);
        }

        logRequest(request);
        Instant start = Instant.now();

        return chain.filter(exchange)
                    .doOnSuccess(_ -> {
                        Duration duration = Duration.between(start, Instant.now());
                        logResponse(exchange.getResponse(), duration);
                    });
    }

    private void logRequest(ServerHttpRequest request) {
        String method = request.getMethod().name();
        String path = request.getPath().value();
        String query = request.getURI().getQuery();
        String contentType = Optional.ofNullable(request.getHeaders().getContentType())
                                     .map(MediaType::toString)
                                     .orElse("");
        long contentLength = request.getHeaders().getContentLength();
        String httpVersion = "HTTP/1.1";

        if (request.getHeaders().containsHeader(SCHEME.text().toString())) {
            httpVersion = "HTTP/2.0";
        }

        log.debug("REQUEST: {} {}{} {} contentType={} contentLength={}",
            method, path, query, httpVersion, contentType, contentLength
        );
    }

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