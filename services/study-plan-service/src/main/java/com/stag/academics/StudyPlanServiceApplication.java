package com.stag.academics;

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
        CodelistServiceGrpc.CodelistServiceBlockingStub.class
    }
)
@SpringBootApplication
public class StudyPlanServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyPlanServiceApplication.class, args);
    }

}
