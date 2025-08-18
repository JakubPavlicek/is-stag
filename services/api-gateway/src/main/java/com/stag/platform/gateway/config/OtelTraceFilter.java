package com.stag.platform.gateway.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapSetter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_PREDICATE_MATCHED_PATH_ATTR;

@Component
public class OtelTraceFilter implements GlobalFilter, Ordered {

    // TextMapSetter to inject headers into the request
    private static final TextMapSetter<ServerHttpRequest.Builder> setter =
        (carrier, key, value) -> Optional.ofNullable(carrier)
                                         .ifPresent(builder -> builder.header(key, value));

    private final OpenTelemetry openTelemetry;

    public OtelTraceFilter(OpenTelemetry openTelemetry) {
        this.openTelemetry = openTelemetry;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Get the current context, which should contain the SERVER span created by auto-instrumentation
        final Context currentContext = Context.current();
        final Span span = Span.fromContext(currentContext);

        // Update the span name and http.route attribute
        updateSpanAttributes(span, exchange);

        // Create a mutable request builder to add the trace headers
        ServerHttpRequest.Builder requestBuilder = exchange.getRequest()
                                                           .mutate();

        // Inject the current context into the request headers
        // This will propagate the traceparent header from the existing SERVER span
        openTelemetry.getPropagators()
                     .getTextMapPropagator()
                     .inject(currentContext, requestBuilder, setter);

        // Continue the filter chain with the modified request
        ServerWebExchange modifiedRequest = exchange.mutate()
                                                    .request(requestBuilder.build())
                                                    .build();

        return chain.filter(modifiedRequest);
    }

    @Override
    public int getOrder() {
        // This filter needs to run after the initial OpenTelemetry filter
        // but before the routing filter sends the request downstream
        return -1;
    }

    private void updateSpanAttributes(Span span, ServerWebExchange exchange) {
        String gatewayPath = exchange.getAttribute(GATEWAY_PREDICATE_MATCHED_PATH_ATTR);

        if (gatewayPath != null) {
            String newName = exchange.getRequest().getMethod().name() + " " + gatewayPath;
            span.updateName(newName);
            span.setAttribute("http.route", gatewayPath);
        }
    }

}