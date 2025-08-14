package com.stag.identity;

import com.stag.platform.grpc.config.AsyncConfig;
import com.stag.platform.grpc.config.GrpcConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Import({ GrpcConfig.class, AsyncConfig.class})
@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
