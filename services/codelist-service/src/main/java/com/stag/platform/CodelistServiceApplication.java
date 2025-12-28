package com.stag.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

/// **Codelist Service Application**
///
/// The main entry point for the Codelist microservice that manages reference data including
/// addresses, countries, educational institutions, and domain-based codelist entries.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@EnableAsync
@EnableCaching
@SpringBootApplication
public class CodelistServiceApplication {

    /// Application entry point that bootstraps the Spring Boot context.
    ///
    /// @param args Command-line arguments
    public static void main(String[] args) {
        SpringApplication.run(CodelistServiceApplication.class, args);
    }

}
