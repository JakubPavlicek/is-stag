package com.stag.academics;

import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import grpcstarter.client.EnableGrpcClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/// **Study Plan Service Application**
///
/// Main entry point for the Study Plan microservice. Manages study programs,
/// study plans, and fields of study with gRPC integration to Codelist service.
/// Enables async processing and caching for improved performance.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@EnableAsync
@EnableCaching
@EnableGrpcClients(
    clients = {
        CodelistServiceGrpc.CodelistServiceBlockingStub.class
    }
)
@SpringBootApplication
public class StudyPlanServiceApplication {

    /// Main method to launch the Study Plan Service application.
    ///
    /// @param args command-line arguments
    public static void main(String[] args) {
        SpringApplication.run(StudyPlanServiceApplication.class, args);
    }

}
