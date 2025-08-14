package com.stag.platform.grpc.config;

import io.opentelemetry.context.Context;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.util.concurrent.Executor;

/// Configuration class to configure asynchronous execution with OpenTelemetry context propagation.
/// Used for TraceId propagation in @Async methods for gRPC calls
@Configuration
public class AsyncConfig implements AsyncConfigurer {

    private final Executor applicationTaskExecutor;

    /// Use the Spring's executor (the one activated when Virtual Threads are enabled)
    public AsyncConfig(
        @Qualifier("applicationTaskExecutor") Executor applicationTaskExecutor
    ) {
        this.applicationTaskExecutor = applicationTaskExecutor;
    }

    /// Return modified Spring's executor wrapped in the current OpenTelemetry context
    @Override
    public Executor getAsyncExecutor() {
        return command -> applicationTaskExecutor.execute(Context.current().wrap(command));
    }

}
