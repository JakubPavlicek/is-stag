package com.stag.academics;

import com.stag.platform.grpc.config.AsyncConfig;
import com.stag.platform.grpc.config.GrpcConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Import({GrpcConfig.class, AsyncConfig.class})
@SpringBootApplication
public class StudentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentServiceApplication.class, args);
    }

}
