package com.stag.platform.codelist.config;

import build.buf.protovalidate.Validator;
import build.buf.protovalidate.ValidatorFactory;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class GrpcConfig {

    @Bean
    public Executor grpcExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @GrpcGlobalServerInterceptor
    ProtoValidationInterceptor protoValidationInterceptor() {
        Validator validator = ValidatorFactory.newBuilder().build();
        return new ProtoValidationInterceptor(validator);
    }

}
