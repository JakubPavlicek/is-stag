package com.stag.platform.gateway.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Component
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Skip logging for non-API requests
        if (!httpRequest.getRequestURI().startsWith("/api")) {
            chain.doFilter(request, response);
            return;
        }

        logRequest(httpRequest);
        Instant start = Instant.now();

        try {
            chain.doFilter(request, response);
        } finally {
            Duration duration = Duration.between(start, Instant.now());
            logResponse(httpResponse, duration);
        }
    }

    private void logRequest(HttpServletRequest request) {
        String method = request.getMethod();
        String path = request.getRequestURI();
        String query = request.getQueryString() != null ? "?" + request.getQueryString() : "";
        String contentType = Optional.ofNullable(request.getContentType()).orElse("");
        int contentLength = request.getContentLength();

        String httpVersion = request.getProtocol();

        log.debug("REQUEST: {} {}{} {} contentType={} contentLength={}",
            method, path, query, httpVersion, contentType, contentLength
        );
    }

    private void logResponse(HttpServletResponse response, Duration duration) {
        int status = response.getStatus();
        long millis = duration.toMillis();
        String contentType = Optional.ofNullable(response.getContentType())
                                     .map(ct -> {
                                         try {
                                             return MediaType.parseMediaType(ct).getType();
                                         } catch (Exception e) {
                                             return ct;
                                         }
                                     })
                                     .orElse("");

        // Note: Content length may not be available until a response is committed
        String contentLengthHeader = response.getHeader("Content-Length");
        String contentLength = contentLengthHeader != null ? contentLengthHeader : "unknown";

        String httpVersion = "HTTP/1.1"; // Default, a protocol version isn't easily accessible in servlet response

        log.debug("RESPONSE: {} {} duration={}ms contentType={} contentLength={}",
            status, httpVersion, millis, contentType, contentLength
        );
    }

}