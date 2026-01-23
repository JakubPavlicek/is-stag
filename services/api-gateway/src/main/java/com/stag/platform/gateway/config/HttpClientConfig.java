package com.stag.platform.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;

@Configuration
public class HttpClientConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.create()
                         .protocol(HttpProtocol.H2C, HttpProtocol.HTTP11)
                         .http2Settings(spec -> spec
                             .maxConcurrentStreams(100)
                             .initialWindowSize(1048576)
                         );
    }

}
