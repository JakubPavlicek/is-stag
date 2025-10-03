package com.stag.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableCaching
@SpringBootApplication
public class CodelistServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodelistServiceApplication.class, args);
    }

}
