package com.stag.identity;

import com.stag.academics.student.v1.StudentServiceGrpc;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import grpcstarter.client.EnableGrpcClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@EnableGrpcClients(
    clients = {
        StudentServiceGrpc.StudentServiceBlockingStub.class,
        CodelistServiceGrpc.CodelistServiceBlockingStub.class
    }
)
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
