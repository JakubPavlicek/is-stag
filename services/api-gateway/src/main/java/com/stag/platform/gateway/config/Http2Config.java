package com.stag.platform.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class Http2Config {

    /// Configure Java HttpClient with HTTP/2 support.
    /// This client will be used by Spring Cloud Gateway MVC for proxying requests.
    @Bean
    public HttpClient httpClient() {
        return HttpClient.newBuilder()
                         .version(HttpClient.Version.HTTP_2)
                         .connectTimeout(Duration.ofSeconds(5))
                         .build();
    }

}
