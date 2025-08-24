package com.stag.platform.gateway.config;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class Http2Config {

    @Bean
    public ConnectionProvider connectionProvider() {
        return ConnectionProvider.builder("api-gateway-http2-pool")
                                 .maxConnections(1000)
                                 .maxIdleTime(Duration.ofSeconds(60))
                                 .maxLifeTime(Duration.ofMinutes(10))
                                 .pendingAcquireTimeout(Duration.ofSeconds(45))
                                 .build();
    }

    @Bean
    public HttpClient httpClient(ConnectionProvider connectionProvider) {
        return HttpClient.create(connectionProvider)
                         .protocol(HttpProtocol.H2C, HttpProtocol.HTTP11)
                         .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                         .responseTimeout(Duration.ofSeconds(15))
                         .compress(true)
                         .wiretap(true);
    }


}
