package com.stag.academics;

import com.stag.academics.studyplan.v1.StudyPlanServiceGrpc;
import com.stag.identity.person.v1.PersonServiceGrpc;
import grpcstarter.client.EnableGrpcClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/// **Student Service Application**
///
/// Main entry point for the Student microservice. Manages student data.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
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

    /// Main entry point of the application.
    ///
    /// @param args command line arguments
    public static void main(String[] args) {
        SpringApplication.run(StudentServiceApplication.class, args);
    }

}
