package com.stag.identity;

import com.stag.academics.student.v1.StudentServiceGrpc;
import com.stag.platform.codelist.v1.CodelistServiceGrpc;
import grpcstarter.client.EnableGrpcClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/// **User Service Application**
///
/// Main entry point for the User/Identity microservice.
/// Manages person entities, profiles, addresses, education, and banking information.
/// Integrates with Student and Codelist services via gRPC.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
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

    /// Main method to launch the User Service application.
    ///
    /// @param args command-line arguments
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
