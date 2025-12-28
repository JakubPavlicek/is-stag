package com.stag.academics;

import com.stag.academics.studyplan.v1.StudyPlanServiceGrpc;
import com.stag.identity.person.v1.PersonServiceGrpc;
import grpcstarter.client.EnableGrpcClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@EnableGrpcClients(
    clients = {
        StudyPlanServiceGrpc.StudyPlanServiceBlockingStub.class,
        PersonServiceGrpc.PersonServiceBlockingStub.class
    }
)
@SpringBootApplication
public class StudentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentServiceApplication.class, args);
    }

}
