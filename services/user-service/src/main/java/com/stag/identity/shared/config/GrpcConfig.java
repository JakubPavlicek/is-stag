package com.stag.identity.shared.config;

import build.buf.protovalidate.Validator;
import build.buf.protovalidate.ValidatorFactory;
import com.stag.platform.grpc.validation.ProtoValidateClientInterceptor;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
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

    @GrpcGlobalClientInterceptor
    ProtoValidateClientInterceptor protoValidationInterceptor() {
        Validator validator = ValidatorFactory.newBuilder().build();
        return new ProtoValidateClientInterceptor(validator);
    }

}