package com.stag.platform.codelist.config;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class GrpcConfig {

    @Bean
    public Executor grpcExecutor() {
        return Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 2,
            new ThreadFactoryBuilder().setNameFormat("grpc-codelist-pool-%d").build()
        );
    }

}
