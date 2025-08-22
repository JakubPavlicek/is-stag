package com.stag.identity.shared.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Wrap request/response to cache content
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // Skip logging for non-API requests
        if (!requestWrapper.getRequestURI().startsWith("/api")) {
            filterChain.doFilter(request, response);
            return;
        }

        logRequest(requestWrapper);
        Instant start = Instant.now();

        filterChain.doFilter(requestWrapper, responseWrapper);

        Duration duration = Duration.between(start, Instant.now());
        logResponse(responseWrapper, duration);

        // Copy cached response back to the original response
        responseWrapper.copyBodyToResponse();
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();
        String queryString = Optional.ofNullable(request.getQueryString()).orElse("");
        String contentType = Optional.ofNullable(request.getContentType()).orElse("");
        int contentLength = request.getContentLength();

        log.debug("REQUEST: {} {}{} contentType={} contentLength={}",
            method, requestURI, queryString, contentType, contentLength
        );
    }

    private void logResponse(ContentCachingResponseWrapper response, Duration duration) {
        int status = response.getStatus();
        long millis = duration.toMillis();
        String contentType = Optional.ofNullable(response.getContentType()).orElse("");
        int contentLength = response.getContentSize();

        log.debug("RESPONSE: {} duration={}ms contentType={} contentLength={}",
            status, millis, contentType, contentLength
        );
    }

}
