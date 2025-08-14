package com.stag.platform.gateway.config;

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.contrib.sampler.RuleBasedRoutingSampler;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.semconv.UrlAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OtelConfig {

    @Bean
    public AutoConfigurationCustomizerProvider otelCustomizer() {
        return p -> p.addSamplerCustomizer((fallback, _) ->
            RuleBasedRoutingSampler.builder(SpanKind.SERVER, fallback)
                                   .drop(UrlAttributes.URL_PATH, "^/actuator")
                                   .build()
        );
    }

}
