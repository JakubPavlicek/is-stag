package com.stag.platform.gateway.config;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.util.concurrent.TimeUnit;

@Configuration
public class HttpConfig {

    @Bean
    public ClientHttpRequestFactory apacheClientHttpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    private CloseableHttpClient httpClient() {
        ConnectionConfig connConfig =
            ConnectionConfig.custom()
                            .setConnectTimeout(600000, TimeUnit.MILLISECONDS)
                            .setSocketTimeout(600000, TimeUnit.MILLISECONDS)
                            .setValidateAfterInactivity(600000, TimeUnit.MILLISECONDS)
                            .build();

        HttpClientConnectionManager connectionManager =
            PoolingHttpClientConnectionManagerBuilder.create()
                                                     .setDefaultConnectionConfig(connConfig)
                                                     .setMaxConnTotal(200)
                                                     .setMaxConnPerRoute(200)
                                                     .build();
        return HttpClients.custom()
                          .setConnectionManager(connectionManager)
                          .build();
    }

}
