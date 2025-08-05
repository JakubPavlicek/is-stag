package com.stag.platform.codelist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CodelistServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodelistServiceApplication.class, args);
    }

}
