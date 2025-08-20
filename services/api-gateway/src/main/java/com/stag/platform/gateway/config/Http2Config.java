package com.stag.platform.gateway.config;

import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.HttpProtocol;

@Configuration
public class Http2Config {

    @Bean
    public HttpClientCustomizer httpClientCustomizer() {
        return httpClient -> httpClient.protocol(HttpProtocol.H2C, HttpProtocol.HTTP11);
    }

}
