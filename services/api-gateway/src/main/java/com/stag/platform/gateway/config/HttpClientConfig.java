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

/// **HTTP Client Configuration**
///
/// Configures Apache HttpClient 5 with custom connection pooling and timeout settings for optimal performance in the API Gateway.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
@Configuration
public class HttpClientConfig {

    /// Creates a configured HTTP request factory using Apache HttpComponents.
    ///
    /// This factory is used by Spring's `RestTemplate` and other HTTP clients
    /// to create HTTP requests with the custom connection pool and timeout settings.
    ///
    /// @return Configured `ClientHttpRequestFactory` with Apache HttpClient
    @Bean
    public ClientHttpRequestFactory apacheClientHttpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    /// Builds and configures the Apache HttpClient with connection pooling and timeouts.
    ///
    /// @return Configured `CloseableHttpClient` instance
    private CloseableHttpClient httpClient() {
        // Configure connection timeouts and validation
        ConnectionConfig connConfig =
            ConnectionConfig.custom()
                            .setConnectTimeout(600000, TimeUnit.MILLISECONDS)
                            .setSocketTimeout(600000, TimeUnit.MILLISECONDS)
                            .setValidateAfterInactivity(600000, TimeUnit.MILLISECONDS)
                            .build();

        // Build connection pool with custom configuration
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
