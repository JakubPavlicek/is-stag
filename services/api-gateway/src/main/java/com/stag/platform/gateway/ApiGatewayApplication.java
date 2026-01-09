package com.stag.platform.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/// **API Gateway Application**
///
/// Main entry point for the Spring Cloud Gateway application.
/// This gateway serves as the single entry point for all client requests, routing them to appropriate backend microservices.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@SpringBootApplication
public class ApiGatewayApplication {

    /// Application entry point.
    ///
    /// Bootstraps the Spring Boot application with all configured beans, security filters, and gateway routes.
    ///
    /// @param args Command-line arguments (not used)
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

}
