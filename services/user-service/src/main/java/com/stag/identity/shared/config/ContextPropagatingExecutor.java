package com.stag.identity.shared.config;

import io.opentelemetry.context.Context;
import org.springframework.lang.NonNull;

import java.util.concurrent.Executor;

public class ContextPropagatingExecutor implements Executor {
    private final Executor delegate;

    public ContextPropagatingExecutor(Executor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void execute(@NonNull Runnable command) {
        Context context = Context.current();
        delegate.execute(() -> {
            try (var _ = context.makeCurrent()) {
                command.run();
            }
        });
    }
}
