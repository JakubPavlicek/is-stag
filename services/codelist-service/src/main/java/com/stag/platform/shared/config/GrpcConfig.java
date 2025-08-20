package com.stag.platform.shared.config;

import build.buf.protovalidate.Validator;
import build.buf.protovalidate.ValidatorFactory;
import com.stag.platform.grpc.validation.ProtoValidateClientInterceptor;
import com.stag.platform.grpc.validation.ProtoValidateServerInterceptor;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcConfig {

    /// Protovalidate server-side interceptor for gRPC requests
    @GrpcGlobalServerInterceptor
    ProtoValidateServerInterceptor protoValidationServerInterceptor() {
        Validator validator = ValidatorFactory.newBuilder().build();
        return new ProtoValidateServerInterceptor(validator);
    }

    /// Protovalidate client-side interceptor for gRPC requests
    @GrpcGlobalClientInterceptor
    ProtoValidateClientInterceptor protoValidationClientInterceptor() {
        Validator validator = ValidatorFactory.newBuilder().build();
        return new ProtoValidateClientInterceptor(validator);
    }

}