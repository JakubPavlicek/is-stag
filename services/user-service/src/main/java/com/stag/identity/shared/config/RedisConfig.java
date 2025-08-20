package com.stag.identity.shared.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stag.identity.person.model.PersonAddresses;
import com.stag.identity.person.model.PersonBanking;
import com.stag.identity.person.model.PersonEducation;
import com.stag.identity.person.model.PersonProfile;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.time.Duration;

import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());

        return builder -> builder
            .withCacheConfiguration("person_profile", getRedisCacheConfig(objectMapper, PersonProfile.class))
            .withCacheConfiguration("person_addresses", getRedisCacheConfig(objectMapper, PersonAddresses.class))
            .withCacheConfiguration("person_education", getRedisCacheConfig(objectMapper, PersonEducation.class))
            .withCacheConfiguration("person_banking", getRedisCacheConfig(objectMapper, PersonBanking.class));

    }

    private <T> RedisCacheConfiguration getRedisCacheConfig(ObjectMapper objectMapper, Class<T> clazz) {
        Jackson2JsonRedisSerializer<T> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, clazz);

        return RedisCacheConfiguration.defaultCacheConfig()
                                      .entryTtl(Duration.ofMinutes(1))
                                      .serializeValuesWith(fromSerializer(serializer));
    }

}
