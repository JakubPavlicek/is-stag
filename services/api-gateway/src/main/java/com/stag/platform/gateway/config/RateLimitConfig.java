package com.stag.platform.gateway.config;

import io.github.bucket4j.distributed.proxy.AsyncProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    // TODO: keyResolver Bean to retrieve userId from JWT token
    // TODO: Add rateLimiter to the routes config

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Bean(destroyMethod = "shutdown")
    public RedisClient redisClient() {
        return RedisClient.create("redis://" + redisHost + ":" + redisPort);
    }

    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<String, byte[]> redisConnection(RedisClient redisClient) {
        return redisClient.connect(RedisCodec.of(new StringCodec(), new ByteArrayCodec()));
    }

    @Bean
    public AsyncProxyManager<String> redisProxyManager(StatefulRedisConnection<String, byte[]> connection) {
        RedisAsyncCommands<String, byte[]> asyncCommands = connection.async();

        LettuceBasedProxyManager<String> proxyManager = Bucket4jLettuce.casBasedBuilder(asyncCommands)
                                                                       .build();

        return proxyManager.asAsync();
    }

}
