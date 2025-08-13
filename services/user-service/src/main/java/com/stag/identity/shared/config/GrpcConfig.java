package com.stag.identity.shared.config;

import build.buf.protovalidate.Validator;
import build.buf.protovalidate.ValidatorFactory;
import com.stag.platform.grpc.validation.ProtoValidateClientInterceptor;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.grpc.v1_6.GrpcTelemetry;
import net.devh.boot.grpc.client.channelfactory.GrpcChannelConfigurer;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

    /// Protovalidate client interceptor used for validating gRPC requests
    @GrpcGlobalClientInterceptor
    ProtoValidateClientInterceptor protoValidationInterceptor() {
        Validator validator = ValidatorFactory.newBuilder().build();
        return new ProtoValidateClientInterceptor(validator);
    }

    /// @see <a href="https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/main/instrumentation/grpc-1.6/library/README.md">Library Instrumentation for gRPC</a>
    @Bean
    public GrpcServerConfigurer otelGrpcServerConfigurer(OpenTelemetry openTelemetry) {
        return serverBuilder -> serverBuilder.intercept(
            GrpcTelemetry.create(openTelemetry).newServerInterceptor()
        );
    }

    /// @see <a href="https://github.com/open-telemetry/opentelemetry-java-instrumentation/blob/main/instrumentation/grpc-1.6/library/README.md">Library Instrumentation for gRPC</a>
    @Bean
    public GrpcChannelConfigurer otelGrpcChannelConfigurer(OpenTelemetry openTelemetry) {
        return (managedChannelBuilder, s) -> managedChannelBuilder.intercept(
            GrpcTelemetry.create(openTelemetry).newClientInterceptor()
        );
    }

}