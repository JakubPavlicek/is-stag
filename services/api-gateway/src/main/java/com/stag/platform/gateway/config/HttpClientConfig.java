package com.stag.platform.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;

/// Configures the Reactor Netty [HttpClient] used by the API gateway.
///
/// @author Jakub Pavlicek
/// @version 1.0.0
@Configuration
public class HttpClientConfig {

    /// Creates an HTTP client with HTTP/2 (cleartext) preferred and tuned stream/window settings.
    ///
    /// @return configured Reactor Netty HTTP client
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
